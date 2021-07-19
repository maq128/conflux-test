package test.conflux;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint64;

import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.request.Call;
import conflux.web3j.response.UsedGasAndCollateral;
import conflux.web3j.types.TransactionBuilder;

public class Main {
	final static String CONFLUX_RPC_ENDPOINT = "http://test.confluxrpc.com";
	final static String ACCOUNT_PRIVKEY = "b1c338c2900be2709b529d0bca60603d60f75436c847666beb6f472e03d38e50";
	final static String ACCOUNT_ADDRESS = "cfxtest:aattfk4p2we919nsu0zu2xg8jxjsnf6zxy4j8wr8va";
	final static String CONTRACT_ADDRESS = "CFXTEST:TYPE.CONTRACT:ACAXYBMT5772NE2XSMK7Z7CMAACCVE5AD67XW6312T";

	public static void main(String[] args) throws Exception {
		Main app = new Main();
		app.init();
		app.tryAll();
	}

	Cfx cfx;
	Account account;
	conflux.web3j.types.Address contractAddress;

	List<String> addrs;
	List<BigInteger> amounts;
	byte[] pubkey;

	void init() {
		cfx = Cfx.create(CONFLUX_RPC_ENDPOINT);
		account = Account.create(cfx, ACCOUNT_PRIVKEY);
		contractAddress = new conflux.web3j.types.Address(CONTRACT_ADDRESS);

		addrs = new ArrayList<String>();
		addrs.add("0x14fd94992e7c41ce18c6559359efb61f464747f1");
		addrs.add("0x12a5585096377c6dc21300f08174971c2aa53fc9");

		amounts = new ArrayList<BigInteger>();
		amounts.add(BigInteger.valueOf(3));
		amounts.add(BigInteger.valueOf(4));

		pubkey = new BigInteger("2ec1764059cf9e8957192b11304e53ee25d99e438ec7003f11da7b3ee843d9d3e2049808846ababbbb710bf0734a9971ff9076f08f0add1864e8a0100b74cabe", 16).toByteArray();
	}

	void tryAll() throws Exception {
//		tryWithStaticStruct();
//		tryWithSimpleDynamicStruct();
//		tryWithComplexDynamicStruct();
//		tryWithSimpleParams();
//		tryWithComplexParams();
		tryWithReturns();
	}

	Account.Option prepareCall(String method, Type<?>... args) {
		Function function = new Function(method, Arrays.asList(args), Collections.emptyList());
		String data = FunctionEncoder.encode(function);

		System.out.println("method: " + method);
		System.out.println("data: " + data);

		Call call = new Call();
		call.setTo(contractAddress);
		call.setData(data);
		UsedGasAndCollateral estimation = cfx.estimateGasAndCollateral(call).sendAndGet();

		System.out.println("estimation: " + estimation.toString());

		Account.Option option = new Account.Option()
			.withChainId(cfx.getChainId())
			.withGasPrice(BigInteger.valueOf(1L))
			.withGasLimit(
				new BigDecimal(estimation.getGasUsed())
					.multiply(TransactionBuilder.DEFAULT_GAS_OVERFLOW_RATIO)
					.toBigInteger())
			.withStorageLimit(
				new BigDecimal(estimation.getStorageCollateralized())
					.multiply(TransactionBuilder.DEFAULT_COLLATERAL_OVERFLOW_RATIO)
					.toBigInteger());
		return option;
	}

	void tryWithStaticStruct() throws Exception {
		StaticStructParams params = new StaticStructParams(
			new org.web3j.abi.datatypes.Address(addrs.get(0)),
			new org.web3j.abi.datatypes.generated.Uint64(amounts.get(0))
		);
		Account.Option option = prepareCall("tryWithStaticStruct", params);
		String txHash = account.call(option, contractAddress, "tryWithStaticStruct", params);
		System.out.println("tryWithStaticStruct: " + txHash);

		// data =
		// 07bc35dd
		// 00000000000000000000000014fd94992e7c41ce18c6559359efb61f464747f1
		// 0000000000000000000000000000000000000000000000000000000000000003

		// txHash = 0x685b371deaab347f988cc64a6709036086daa26ab2417dc50f64d667017a546f

		// outcomeStatus: 0
	}

	void tryWithSimpleDynamicStruct() throws Exception {
		SimpleDynamicStructParams params = new SimpleDynamicStructParams(
			pubkey
		);
		Account.Option option = prepareCall("tryWithSimpleDynamicStruct", params);
		String txHash = account.call(option, contractAddress, "tryWithSimpleDynamicStruct", params);
		System.out.println("tryWithSimpleDynamicStruct: " + txHash);

		// data =
		// 0061dab6
		// 0000000000000000000000000000000000000000000000000000000000000020
		// 0000000000000000000000000000000000000000000000000000000000000020
		// 0000000000000000000000000000000000000000000000000000000000000040
		// 2ec1764059cf9e8957192b11304e53ee25d99e438ec7003f11da7b3ee843d9d3
		// e2049808846ababbbb710bf0734a9971ff9076f08f0add1864e8a0100b74cabe

		// txHash = 0x5d2fe120323778f6848a20a126a49b9a524fe51354867a435e336d504eff7de1

		// outcomeStatus: 0
	}

	void tryWithComplexDynamicStruct() throws Exception {
		ComplexDynamicStructParams params = new ComplexDynamicStructParams(
			addrs,
			amounts
		);
		Account.Option option = prepareCall("tryWithComplexDynamicStruct", params);
		String txHash = account.call(option, contractAddress, "tryWithComplexDynamicStruct", params);
		System.out.println("tryWithComplexDynamicStruct: " + txHash);

		// data =
		// d2332e23 - tryWithComplexDynamicStruct((address[],uint64[]))
		// 0000000000000000000000000000000000000000000000000000000000000020
		// 0000000000000000000000000000000000000000000000000000000000000040
		// 00000000000000000000000000000000000000000000000000000000000000a0
		// 0000000000000000000000000000000000000000000000000000000000000002
		// 00000000000000000000000014fd94992e7c41ce18c6559359efb61f464747f1
		// 00000000000000000000000012a5585096377c6dc21300f08174971c2aa53fc9
		// 0000000000000000000000000000000000000000000000000000000000000002
		// 0000000000000000000000000000000000000000000000000000000000000003
		// 0000000000000000000000000000000000000000000000000000000000000004

		// txHash = 0x88157053e57751b63afd6d7edd67e38b702245d79e4879d5315f9429f12fa35e

		// outcomeStatus: 0

		// DynamicStruct.getTypeAsString() 计算出的 method signature 是这样的：
		// 0fe806f0 - tryWithComplexDynamicStruct((dynamicarray,dynamicarray))
		// 这是错误的，所以 tx 提交后无法正确执行！！！
	}

	void tryWithSimpleParams() throws Exception {
		org.web3j.abi.datatypes.Address p1 = new org.web3j.abi.datatypes.Address(addrs.get(0));
		org.web3j.abi.datatypes.generated.Uint64 p2 = new org.web3j.abi.datatypes.generated.Uint64(amounts.get(0));
		Account.Option option = prepareCall("tryWithSimpleParams", p1, p2);
		String txHash = account.call(option, contractAddress, "tryWithSimpleParams", p1, p2);
		System.out.println("tryWithSimpleParams: " + txHash);

		// data =
		// 68ff4bac
		// 00000000000000000000000014fd94992e7c41ce18c6559359efb61f464747f1
		// 0000000000000000000000000000000000000000000000000000000000000003

		// txHash = 0x951ba07ed28d2b9a41b2b69ae2341412a8705323d424b3067d6036904697e257

		// outcomeStatus: 0
	}

	void tryWithComplexParams() throws Exception {
		org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address> p1 = new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(addrs.stream().map(org.web3j.abi.datatypes.Address::new).collect(Collectors.toList()));
		org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint64> p2 = new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint64>(amounts.stream().map(Uint64::new).collect(Collectors.toList()));
		Account.Option option = prepareCall("tryWithComplexParams", p1, p2);
		String txHash = account.call(option, contractAddress, "tryWithComplexParams", p1, p2);
		System.out.println("tryWithComplexParams: " + txHash);

		// data =
		// 7af08274 - tryWithComplexParams(address[],uint64[])
		// 0000000000000000000000000000000000000000000000000000000000000040
		// 00000000000000000000000000000000000000000000000000000000000000a0
		// 0000000000000000000000000000000000000000000000000000000000000002
		// 00000000000000000000000014fd94992e7c41ce18c6559359efb61f464747f1
		// 00000000000000000000000012a5585096377c6dc21300f08174971c2aa53fc9
		// 0000000000000000000000000000000000000000000000000000000000000002
		// 0000000000000000000000000000000000000000000000000000000000000003
		// 0000000000000000000000000000000000000000000000000000000000000004

		// txHash = 0x3737436a1ef68cdaeef486fab55e0b0e64409f937b5f8682500e08c87732b582

		// outcomeStatus: 0
	}

	void tryWithReturns() throws Exception {
		org.web3j.abi.datatypes.Address p1 = new org.web3j.abi.datatypes.Address(addrs.get(0));
		org.web3j.abi.datatypes.generated.Uint64 p2 = new org.web3j.abi.datatypes.generated.Uint64(amounts.get(0));
		Account.Option option = prepareCall("tryWithReturns", p1, p2);
		String txHash = account.call(option, contractAddress, "tryWithReturns", p1, p2);
		System.out.println("tryWithReturns: " + txHash);
	}

	// ---- 以下代码来自 web3j 生成的包装类 ----

	public static class StaticStructParams extends StaticStruct {
		public String addr;

		public BigInteger amount;

		public StaticStructParams(
			String addr,
			BigInteger amount
		) {
			super(
				new org.web3j.abi.datatypes.Address(addr),
				new org.web3j.abi.datatypes.generated.Uint64(amount)
			);
			this.addr = addr;
			this.amount = amount;
		}

		public StaticStructParams(org.web3j.abi.datatypes.Address addr, Uint64 amount) {
			super(addr, amount);
			this.addr = addr.getValue();
			this.amount = amount.getValue();
		}
	}

	public static class SimpleDynamicStructParams extends DynamicStruct {
		public byte[] pubkey;

		public SimpleDynamicStructParams(byte[] pubkey) {
			super(new org.web3j.abi.datatypes.DynamicBytes(pubkey));
			this.pubkey = pubkey;
		}

		public SimpleDynamicStructParams(DynamicBytes pubkey) {
			super(pubkey);
			this.pubkey = pubkey.getValue();
		}
	}

	public static class ComplexDynamicStructParams extends DynamicStruct {
		public List<String> addrs;

		public List<BigInteger> amounts;

		public ComplexDynamicStructParams(
			List<String> addrs,
			List<BigInteger> amounts
		) {
//			super(
//				new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(addrs),
//				new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint64>(amounts)
//			);
			// 自动生成的代码带有语法错误，手工修改
			super(
				new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(org.web3j.abi.datatypes.Address.class, addrs.stream().map(org.web3j.abi.datatypes.Address::new).collect(Collectors.toList())),
				new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint64>(Uint64.class, amounts.stream().map(Uint64::new).collect(Collectors.toList()))
			);

			this.addrs = addrs;
			this.amounts = amounts;
		}

		public ComplexDynamicStructParams(
			DynamicArray<org.web3j.abi.datatypes.Address> addrs,
			DynamicArray<Uint64> amounts
		) {
			super(addrs, amounts);
//			this.addrs = addrs.getValue();
//			this.amounts = amounts.getValue();
			// 自动生成的代码带有语法错误，手工修改
			this.addrs = addrs.getValue().stream().map(org.web3j.abi.datatypes.Address::getValue).collect(Collectors.toList());
			this.amounts = amounts.getValue().stream().map(Uint64::getValue).collect(Collectors.toList());
		}

		@Override
		public String getTypeAsString() {
			List<String> list = new ArrayList<String>();
			for (Type val : value) {
				list.add(val.getTypeAsString());
			}
			return "(" + String.join(",", list) + ")";
		}
	}
}
