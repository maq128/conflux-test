const fs = require('fs');
const jssdk = require('js-conflux-sdk')

const Test = artifacts.require("Test");

module.exports = async function (deployer) {
	await deployer.deploy(Test);

  let contract = await Test.deployed();

  var network = deployer.networks[deployer.network];
  var networkId = network.network_id;
  var rpcEndpoint = `http://${network.host}:${network.port}`;
  var deployerAddress = jssdk.format.hexAddress(network.from);
  var deployerPrivateKey = network.privateKeys[0];
  var contractAddress = contract.address;
  var contractHexAddress = jssdk.format.hexAddress(contract.address);
  var props = [
    `network-id=${networkId}`,
    `rpc-endpoint=${rpcEndpoint}`,
    `deployer-address=${deployerAddress}`,
    `deployer-private-key=${deployerPrivateKey}`,
    `contract-address=${contractAddress}`,
    `contract-hex-address=${contractHexAddress}`,
  ].join('\n');
  fs.writeFileSync(`./build/deployed_${networkId}.properties`, props);
  console.log(`-- deployed_${networkId}.properties --\n` + props);
};
