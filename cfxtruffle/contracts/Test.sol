// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract Test {
    address addr;
    uint64 amount;

    // --------------------------------

    struct StaticStructParams {
        address addr;
        uint64 amount;
    }

    function tryWithStaticStruct(
        StaticStructParams memory params
    ) external {
        require(params.addr != address(0), "params.addr must not be 0");
        require(params.amount != 0, "params.amount must not be 0");
        addr = params.addr;
        amount = params.amount;
    }

    // --------------------------------

    struct SimpleDynamicStructParams {
        bytes pubkey;
    }

    function tryWithSimpleDynamicStruct(
        SimpleDynamicStructParams memory params
    ) external {
        require(params.pubkey.length == 64, "params.pubkey.length must be 64");
        addr = address(bytes20(keccak256(params.pubkey)));
    }

    // --------------------------------

    struct ComplexDynamicStructParams {
        address[] addrs;
        uint64[] amounts;
    }

    function tryWithComplexDynamicStruct(
        ComplexDynamicStructParams memory params
    ) external {
        require(params.addrs.length == 2, "params.addrs.length must be 2");
        require(params.amounts.length == 2, "params.amounts.length must be 2");
        addr = params.addrs[1];
        amount = params.amounts[1];
    }

    // --------------------------------

    function tryWithSimpleParams(
        address paddr,
        uint64 pamount
    ) external {
        require(paddr != address(0), "paddr must not be 0");
        require(pamount != 0, "pamount must not be 0");
        addr = paddr;
        amount = pamount;
    }

    // --------------------------------

    function tryWithComplexParams(
        address[] memory addrs,
        uint64[] memory amounts
    ) external {
        require(addrs.length == 2, "addrs.length must be 2");
        require(amounts.length == 2, "amounts.length must be 2");
        addr = addrs[0];
        amount = amounts[0];
    }
}
