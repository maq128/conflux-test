对比测试 Conflux 智能合约接口使用 struct 类型参数的各种情况。

# 合约部分

用 cfxtruffle 对合约进行编译、部署。还可以用 web3j 生成 Java 的合约包装类。

```sh
cfxtruffle init
npm install js-conflux-sdk

cfxtruffle compile
cfxtruffle deploy --network testnet
cfxtruffle deploy --network testnet --f 2

web3j generate truffle -t build/contracts/Test.json -o build/java -p test.conflux
```

# 使用 js-conflux-sdk 辅助测试

用浏览器打开 [任意一个 `http://` 的网页](http://test.confluxrpc.com) 并进入 devtools 的 console
（`file://` 网页也可以，但是 `https://` 不行）：

```js
// 装载 js-conflux-sdk
(function() {
  var e = document.createElement('script')
  e.setAttribute('type', 'text/javascript')
  e.setAttribute('src', 'https://cdn.jsdelivr.net/npm/js-conflux-sdk/dist/js-conflux-sdk.umd.min.js')
  document.body.appendChild(e)
})()

// 初始化
var SDK = window.Conflux
var sdk = new SDK.Conflux()
sdk.provider = SDK.providerFactory({ url:'http://test.confluxrpc.com' })
var { chainId } = await sdk.getStatus()

console.log('sdk.version:', sdk.version)
console.log('conflux version:', await sdk.getClientVersion())

// 创建一个新的 Account
var privKey = SDK.sign.randomPrivateKey()
var pubKey = SDK.sign.privateKeyToPublicKey(privKey)
var address = SDK.sign.privateKeyToAddress(privKey)
console.log('privKey:', SDK.format.hex(privKey))
console.log('pubKey:', SDK.format.hex(pubKey))
console.log('address:', SDK.format.hexAddress(address))
console.log('address:', SDK.format.address(address, chainId, false))
console.log('address:', SDK.format.address(address, chainId, true))

// 查看账户余额
var balance = await sdk.getBalance('cfxtest:aattfk4p2we919nsu0zu2xg8jxjsnf6zxy4j8wr8va')
console.log('balance:', SDK.Drip(balance).toCFX())

// 获取合约代码
await sdk.getCode('CFXTEST:TYPE.CONTRACT:ACHYDC79W4V83VKTDM4G02M9JEBD6H98P20ADA6NVK')

// 查看 pending transactions
await sdk.getAccountPendingTransactions('cfxtest:aattfk4p2we919nsu0zu2xg8jxjsnf6zxy4j8wr8va')

// 查看 tx 状态
await sdk.getTransactionByHash('0x88157053e57751b63afd6d7edd67e38b702245d79e4879d5315f9429f12fa35e')
await sdk.getTransactionReceipt('0x88157053e57751b63afd6d7edd67e38b702245d79e4879d5315f9429f12fa35e')
```

# 参考资料

[Conflux Developer](https://developer.confluxnetwork.org/)
	| [JSON RPC](http://developer.confluxnetwork.org/conflux-doc/docs/json_rpc/)

[java-conflux-sdk](https://github.com/Conflux-Chain/java-conflux-sdk)

[js-conflux-sdk](https://confluxnetwork.gitbook.io/js-conflux-sdk/)
	| [github](https://github.com/Conflux-Chain/js-conflux-sdk)

[conflux-truffle](https://github.com/Conflux-Chain/conflux-truffle)
	| [conflux-101](https://github.com/Pana/conflux-101)
	| [Truffle 官方文档](https://www.trufflesuite.com/docs/truffle/overview)
	| [Truffle 中文文档](https://learnblockchain.cn/docs/truffle/index.html)
