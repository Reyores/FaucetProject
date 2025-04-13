🚰 Polygon Native POL Faucet API

Ce projet permet de fournir automatiquement une petite quantité de MATIC (POL natif) sur le testnet Polygon Amoy à une adresse spécifiée via une API REST.

📦 Technologies utilisées

🧠 Java 17 + Spring Boot

⚙️ Web3j

🌐 API REST avec POST /faucet

🔐 Smart Contract Solidity NativePOLFaucet

🚀 Déploiement sur Polygon Amoy (chainId: 80002)

🛠️ Pré-requis

Java 17+

Maven

Node.js 18+ (pour déployer le contrat avec Hardhat)

Un wallet avec du POL (MATIC) sur le réseau Amoy

Postman

⚖️ Configuration

🚀 Lancer l’application

mvn clean install
mvn spring-boot:run

L’application démarre sur :📍 http://localhost:8080

📬 Utiliser l’API avec Postman

🔚 Endpoint : POST /api/faucet

🔸 Requête

URL : http://localhost:8080/api/faucet

Méthode : POST

Body (raw JSON) :

{
  "address": "0xVotreAdresseTestnetIci"
}

🔸 Réponse (succès)

Tokens envoyés avec succès. Transaction hash : 0x1234abc...

🔸 Réponse (erreur)

Erreur lors de l'envoi des tokens : Adresse invalide ou rate-limit atteint.


📜 Contrat déployé

Le contrat est minimal :

contract NativePOLFaucet {
    function sendPOL(address payable recipient, uint256 amount) public {
        require(address(this).balance >= amount, "Faucet: Not enough balance");
        recipient.transfer(amount);
    }

    receive() external payable {}
}


