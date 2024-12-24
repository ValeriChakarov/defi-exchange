package com.example.defiExchange.service;

import com.example.defiExchange.model.DeFiProtocol;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.springframework.stereotype.Service;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class DeFiService {

    private final Web3j web3j;
    private static final Logger logger = Logger.getLogger(DeFiService.class.getName());

    // Uniswap Factory Contract Address (on Ethereum Mainnet)
    private static final String UNISWAP_FACTORY_ADDRESS = "0x5C69bEe701ef814a2B6a3EDD4B1652CB9cc5aA6f";
    // Updated Ethereum Mainnet Multicall contract address
    private static final String MULTICALL_CONTRACT_ADDRESS = "0x1F98415757620B543A52E61c46B32eB19261F984";  // Multicall contract address for Ethereum Mainnet


    public DeFiService() {
        String infuraUrl = "https://mainnet.infura.io/v3/77b5d15db1a64d03bc3ac0705d189c01"; // Replace with your Infura URL or local node URL
        this.web3j = Web3j.build(new HttpService(infuraUrl)); // Web3j instance to connect to Ethereum
    }

    // Your method to call the Multicall contract and get Uniswap trading pairs
    public List<String> getUniswapTradingPairs() throws Exception {
        List<String> tradingPairs = new ArrayList<>();

        // Define the function signature for the "allPairsLength" method of the Uniswap Factory contract
        Function function = new Function(
                "allPairsLength",  // The function name
                new ArrayList<>(),  // No input parameters
                List.of(new TypeReference<Uint256>() {})  // Return type Uint256
        );

        // Encode the function
        String data = FunctionEncoder.encode(function);

        // Create the transaction to call the Ethereum contract
        Transaction transaction = Transaction.createEthCallTransaction(
                UNISWAP_FACTORY_ADDRESS,  // Address of the contract
                UNISWAP_FACTORY_ADDRESS,  // The contract address (same as the sender address for a read call)
                data  // Encoded data for the function call
        );

        // Make the Ethereum call to get the number of trading pairs
        EthCall response = web3j.ethCall(transaction, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();

        // Check for errors in the response
        if (response.hasError()) {
            logger.severe("Error fetching data from Ethereum: " + response.getError().getMessage());
            throw new Exception("Error fetching data from Ethereum: " + response.getError().getMessage());
        }

        // Decode the response and handle empty responses safely
        List<Type> decodedResponse = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if (decodedResponse.isEmpty()) {
            logger.warning("No pairs length data returned from Uniswap contract.");
            return tradingPairs;  // Return empty list if no data is available
        }

        Uint256 allPairsLength = (Uint256) decodedResponse.get(0);  // Get the length of all pairs
        BigInteger numPairs = allPairsLength.getValue();

        // Handle case if no pairs are available
        if (numPairs.equals(BigInteger.ZERO)) {
            logger.warning("No trading pairs found.");
            return tradingPairs;  // Return empty list if no pairs are available
        }

        // Loop through all pairs to get the pair addresses
        for (BigInteger i = BigInteger.ZERO; i.compareTo(numPairs) < 0; i = i.add(BigInteger.ONE)) {
            // Define the function to call allPairs(i)
            Function pairFunction = new Function(
                    "allPairs",   // Function name
                    List.of(new org.web3j.abi.datatypes.generated.Uint256(i)),  // Function input (the index 'i')
                    List.of(new TypeReference<org.web3j.abi.datatypes.Address>() {})  // Return type Address
            );

            // Encode the function
            String pairData = FunctionEncoder.encode(pairFunction);

            // Create the transaction for the pair call
            Transaction pairTransaction = Transaction.createEthCallTransaction(
                    UNISWAP_FACTORY_ADDRESS,  // Address of the contract
                    UNISWAP_FACTORY_ADDRESS,  // The contract address (same as the sender address for a read call)
                    pairData                  // Encoded data for the function call
            );

            // Call the Ethereum network for the pair address
            EthCall pairResponse = web3j.ethCall(pairTransaction, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();

            // Handle empty or erroneous pair response
            if (pairResponse.hasError()) {
                logger.severe("Error fetching pair data from Ethereum: " + pairResponse.getError().getMessage());
                continue;
            }

            // Decode the response to get the trading pair address
            List<Type> pairAddresses = FunctionReturnDecoder.decode(pairResponse.getValue(), pairFunction.getOutputParameters());
            if (pairAddresses.isEmpty()) {
                logger.warning("No pair address found for index " + i);
                continue;
            }

            String pairAddress = pairAddresses.get(0).getValue().toString();  // Get the actual address
            tradingPairs.add(pairAddress);  // Add the actual trading pair address to the list

            // Add a delay to prevent hitting rate limits
            Thread.sleep(500);  // Delay for 500 milliseconds (adjust as needed)
        }

        return tradingPairs;
    }


    // Example method to get the list of DeFi protocols (mock data)
    public List<DeFiProtocol> getDeFiProtocols() {
        // Replace with real API calls if needed
        return List.of(
                new DeFiProtocol("Uniswap", "https://uniswap.org", "Decentralized Exchange (DEX) for swapping tokens", 0.003),
                new DeFiProtocol("SushiSwap", "https://sushi.com", "Decentralized Exchange with yield farming", 0.005)
        );
    }
}
