
---

# Ikonnect Mobile App

## Présentation

**Ikonnect Mobile** est une application Android dédiée à la gestion des messages SMS et MMS. Elle est conçue pour offrir une expérience utilisateur moderne et intuitive en utilisant les dernières technologies Android. Le projet vise à fournir des fonctionnalités complètes pour la gestion des communications par message, avec une interface utilisateur fluide et efficace.

### Objectifs du Projet

- **Gestion Avancée des Messages :** Lire, envoyer et organiser des SMS et MMS.
- **Interface Moderne :** Utilisation de Jetpack Compose pour une interface réactive et esthétique.
- **Performance Optimisée :** Assurer un chargement rapide des messages et une gestion fluide des ressources.

## Installation

Pour installer et exécuter l'application **Ikonnect Mobile**, suivez les étapes ci-dessous :

1. **Cloner le Dépôt**

    Ouvrez un terminal et exécutez la commande suivante pour cloner le dépôt du projet :

    ```bash
    git clone https://github.com/raph2611toky/android.git
    ```

2. **Ouvrir le Projet dans Android Studio**

    - Lancez [Android Studio](https://developer.android.com/studio).
    - Cliquez sur **"Open an existing project"**.
    - Sélectionnez le répertoire que vous venez de cloner.

3. **Configurer les Dépendances**

    - Assurez-vous d'avoir les SDK nécessaires installés ainsi que les plugins requis pour Kotlin et Jetpack Compose.
    - Android Studio proposera automatiquement l'installation des dépendances manquantes si nécessaire.

4. **Construire et Exécuter l'Application**

    - Cliquez sur le bouton **"Run"** dans Android Studio.
    - Sélectionnez un émulateur ou un appareil Android connecté pour exécuter l'application.

## Configuration

L'application **Ikonnect Mobile** nécessite une configuration minimale pour fonctionner correctement :

1. **Permissions**

    Assurez-vous que les permissions nécessaires sont définies dans le fichier `AndroidManifest.xml` pour accéder aux SMS et MMS.

    ```xml
    <uses-permission android:name="android.permission.READ_SMS" />
    <uses-permission android:name="android.permission.SEND_SMS" />
    <uses-permission android:name="android.permission.RECEIVE_MMS" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    ```

2. **Base de Données**

    L'application utilise une base de données locale pour stocker les messages SMS et MMS. Aucune configuration supplémentaire n'est requise pour la base de données, elle est gérée automatiquement par l'application.

## Fonctionnalités

**Ikonnect Mobile** offre les fonctionnalités suivantes :

- **Visualisation des Messages :** Affiche les messages SMS et MMS dans une interface facile à lire.
- **Envoi de Messages :** Permet l'envoi de nouveaux messages directement depuis l'application.
- **Organisation des Messages :** Offre des options pour organiser les messages en fils de discussion.
- **Support des MMS :** Gère les messages multimédia incluant images et vidéos.
- **Notifications :** Recevez des notifications pour les nouveaux messages entrants.

## Contribuer

Si vous souhaitez contribuer à **Ikonnect Mobile**, voici comment vous pouvez le faire :

1. **Forker le Référentiel**

    Forkez le projet en utilisant le bouton **"Fork"** sur GitHub pour créer votre propre copie du dépôt.

2. **Cloner le Référentiel Forké**

    Clonez votre dépôt forké localement en utilisant :

    ```bash
    git clone https://github.com/yourusername/android.git
    ```

3. **Créer une Branche pour votre Fonctionnalité**

    Créez une nouvelle branche pour les modifications :

    ```bash
    git checkout -b feature/your-feature-name
    ```

4. **Effectuer des Modifications et Committer**

    Faites vos modifications, puis ajoutez et commettez vos changements :

    ```bash
    git add .
    git commit -m "Ajout de la fonctionnalité X"
    ```

5. **Pousser les Modifications**

    Poussez vos modifications vers votre dépôt forké :

    ```bash
    git push origin feature/your-feature-name
    ```

6. **Créer une Pull Request**

    Ouvrez une pull request sur le dépôt principal pour que vos modifications soient examinées et potentiellement intégrées.

## Documentation

Pour une documentation détaillée sur la structure de la base de données et les opérations spécifiques liées aux SMS et MMS, consultez le fichier [mmssms_database.md](./mmssms_database.md).

## Contact

Pour toute question ou commentaire, veuillez contacter :

- **Nom :** TOKY Nandrasana
- **Email :** raphaeltokinandrasana@gmail.com
- **GitHub :** [raph2611toky](https://github.com/raph2611toky)

---
