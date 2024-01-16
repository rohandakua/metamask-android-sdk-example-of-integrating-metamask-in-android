package io.metamask.androidsdk

import org.json.JSONObject

class InfuraProvider(private val infuraAPIKey: String) {
    val rpcUrls: Map<String, String> = mapOf(
        // ###### Ethereum ######
        // Mainnet
        "0x1" to "https://mainnet.infura.io/v3/${infuraAPIKey}",
        // Goerli
        "0x5" to "https://goerli.infura.io/v3/${infuraAPIKey}",
        // Sepolia 11155111
        "0x2a" to "https://sepolia.infura.io/v3/${infuraAPIKey}",
        // ###### Polygon ######
        // Mainnet
        "0x89" to "https://polygon-mainnet.infura.io/v3/${infuraAPIKey}",
        // Mumbai
        "0x13881" to "https://polygon-mumbai.infura.io/v3/${infuraAPIKey}",
        // ###### Optimism ######
        // Mainnet
        "0x45" to "https://optimism-mainnet.infura.io/v3/${infuraAPIKey}",
        // Goerli
        "0x1a4" to "https://optimism-goerli.infura.io/v3/${infuraAPIKey}",
        // ###### Arbitrum ######
        // Mainnet
        "0xa4b1" to "https://arbitrum-mainnet.infura.io/v3/${infuraAPIKey}",
        // Goerli
        "0x66eed" to "https://arbitrum-goerli.infura.io/v3/${infuraAPIKey}",
        // ###### Palm ######
        // Mainnet
        "0x2a15c308d" to "https://palm-mainnet.infura.io/v3/${infuraAPIKey}",
        // Testnet
        "0x2a15c3083" to "https://palm-testnet.infura.io/v3/${infuraAPIKey}",
        // ###### Avalanche C-Chain ######
        // Mainnet
        "0xa86a" to "https://avalanche-mainnet.infura.io/v3/${infuraAPIKey}",
        // Fuji
        "0xa869" to "https://avalanche-fuji.infura.io/v3/${infuraAPIKey}",
        // ###### NEAR ######
        // // Mainnet
        // "0x4e454152" to "https://near-mainnet.infura.io/v3/${infuraAPIKey}",
        // // Testnet
        // "0x4e454153" to "https://near-testnet.infura.io/v3/${infuraAPIKey}",
        // ###### Aurora ######
        // Mainnet
        "0x4e454152" to "https://aurora-mainnet.infura.io/v3/${infuraAPIKey}",
        // Testnet
        "0x4e454153" to "https://aurora-testnet.infura.io/v3/${infuraAPIKey}",
        // ###### StarkNet ######
        // Mainnet
        "0x534e5f4d41494e" to "https://starknet-mainnet.infura.io/v3/${infuraAPIKey}",
        // Goerli
        "0x534e5f474f45524c49" to "https://starknet-goerli.infura.io/v3/${infuraAPIKey}",
        // Goerli 2
        "0x534e5f474f45524c4932" to "https://starknet-goerli2.infura.io/v3/${infuraAPIKey}",
        // ###### Celo ######
        // Mainnet
        "0xa4ec" to "https://celo-mainnet.infura.io/v3/${infuraAPIKey}",
        // Alfajores Testnet
        "0xaef3" to "https://celo-alfajores.infura.io/v3/${infuraAPIKey}",
    )

    fun supportsChain(chainId: String): Boolean {
        return !rpcUrls[chainId].isNullOrEmpty()
    }

    fun makeRequest(request: RpcRequest, chainId: String, callback: ((Result) -> Unit)?) {
        val httpClient = HttpClient()
        val params: MutableMap<String, Any> = mutableMapOf()
        params["method"] = request.method
        params["jsonrpc"] = "2.0"
        params["id"] = request.id
        params["params"] = request.params ?: listOf<String>()

        httpClient.newCall("${rpcUrls[chainId]}", parameters = params) { response, ioException ->
            if (response != null) {
                val result = JSONObject(response).optString("result") ?: ""
                callback?.invoke(Result.Success.Item(result))
            } else if (ioException != null) {
                callback?.invoke(Result.Success.Item(ioException.message ?: ""))
            }
        }
    }
}