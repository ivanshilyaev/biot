# Cryptographic data protection in IoT systems

The work examines theoretical and practical aspects of cryptographic 
data protection while using IoT protocols.

Main work results:

- Description of key technologies and technological levels used in 
the Internet of Things.

- Selection of three main protocols: ZigBee, Z-Wave, Wi-Fi — 
and a comparative analysis of their technical characteristics.

- Review of encryption key generation and distribution and data integrity 
control algorithms used in selected protocols, comparative analysis 
of these protocols' security.

- Description of known threats and successful attacks on different 
protocol versions.

- Construction of threat matrix showing potential vulnerability 
of IoT protocols to certain types of cryptographic attacks.

- Implementation of authenticated encryption and hashing algorithms 
from Belarusian cryptographic standard 
[СТБ 34.101.77](http://apmi.bsu.by/assets/files/std/bash-spec24.pdf) 
in two programming languages: Java and C++.

- Firmware development for a smart device in C++ programming language 
with authenticated encryption, as well as smart light bulb prototype 
development running on this firmware. Description of motivation for using 
algorithms from selected standard.

- Web-application development for controlling device in Java programming 
language.

- Detailed review of solution's key technical features, such as
connection establishment, generation and distribution of encryption 
keys, message counter implementation.

- Evaluation of message exchange speed and demonstration of correct 
prototype work.

---

by [@ivanshilyaev](https://github.com/ivanshilyaev), 2021-2022
