Blockchain
==========

> A naive implementation of a Blockchain in Kotlin/JVM.

FOR EDUCATION PURPOSES ONLY.

[![Actions Status](https://github.com/shkschneider/kotlin_Blockchain/workflows/Gradle/badge.svg)](https://github.com/shkschneider/kotlin_Blockchain/actions)

Specifications
--------------

- Kotlin 1.3+ / Java 8 / Gradle 6+
- Supply: 117 bits / 11 700 000 sat
- Block time: unrestricted
- Block size: 10 transactions
- Proof: proof-of-work (partial hash collision)
- Units: bit, cent, milli, micro, sat
- Consensus rules: `Consensus.kt`
  - algorithms: SHA(1), RSA(2048), HMAC(SHA1)
  - blockSize: 10
  - reward: 1.0 bit
  - halving: 10 blocks
  - `origin` KeyPair
  - `genesis` block
  - difficulty: 1 (fixed)

Implementation
--------------

- TransactionOutput (lock/unlockScript)
- Transaction (inputs, outputs) + fees
- Blocks (coinbase + transactions) + proof
- Chain (blocks, mempool, utxos)
- Wallets (private key, public key)
- Miners
- **No network**

Formats
-------

- Addresses: Base58
- Signatures: Base64
- (un)lockScript: Hex
- Proof: Hash
