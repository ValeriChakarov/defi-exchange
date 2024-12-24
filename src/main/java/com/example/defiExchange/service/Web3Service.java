package com.example.defiExchange.service;

import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class Web3Service {

    private final Web3j web3j;

    // Constructor that initializes Web3j using Infura or a local node URL
    public Web3Service() {
        String infuraUrl = "https://mainnet.infura.io/v3/77b5d15db1a64d03bc3ac0705d189c01"; // Replace with your Infura URL or local node URL
        this.web3j = Web3j.build(new HttpService(infuraUrl)); // Web3j instance to connect to Ethereum
    }

    // Example method to get the latest block number
    public String getLatestBlock() throws IOException {
        EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), false).send();
        Block block = ethBlock.getBlock();
        return block.getNumber().toString();
    }

    // Example method to get the current gas price
    public String getGasPrice() throws IOException {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        return ethGasPrice.getGasPrice().toString();
    }

    // Example method to get the Ethereum protocol version (instead of network ID)
    public String getProtocolVersion() throws IOException {
        EthProtocolVersion ethProtocolVersion = web3j.ethProtocolVersion().send();
        return ethProtocolVersion.getProtocolVersion();
    }

    // Method to get Ethereum balance for a specific address
    public BigDecimal getEthBalance(String address) throws IOException {
        EthGetBalance ethBalance = web3j.ethGetBalance(address, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();
        BigInteger wei = ethBalance.getBalance();
        return new BigDecimal(wei).divide(new BigDecimal(1e18)); // Convert wei to ether
    }
}
