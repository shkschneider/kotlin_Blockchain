Blockchain
==========

> A naive implementation of a Blockchain in Kotlin/JVM.

FOR EDUCATION PURPOSES ONLY.

[![Actions Status](https://github.com/shkschneider/kotlin_Blockchain/workflows/Gradle/badge.svg)](https://github.com/shkschneider/kotlin_Blockchain/actions)


Specifications
--------------

- Kotlin 1.3+ / Java 8 / Gradle 5+
- Supply: 118.2029 bits
- Block time: unrestricted
- Block size: 10 transactions
- Proof: proof-of-work (partial hash collision)
- Units: bit, cent, milli, micro, sat
- Consensus rules: `Consensus.kt`
  - algorithms: SHA(1), RSA(2048)
  - blockSize: 10
  - reward: 1.0 bit
  - halving: 10 blocks
  - `origin` KeyPair
  - `genesis` block
  - difficulty: 1 (fixed)

Implementation
--------------

- Working Transactions (inputs, outputs) + fees
- Working Blocks (coinbase + transactions) + proof
- Working Wallets (private key, public key)
- Working Chain (blocks, mempool, utxos)
- Working Node (mining) + Miner
- **No network**
