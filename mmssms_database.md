
---

# Aperçu de la Base de Données `mmssms.db`

La base de données `mmssms.db` utilisée par l'application Messages sur Android contient plusieurs tables importantes, principalement pour stocker les messages SMS, MMS, et les informations connexes. Voici un aperçu des tables principales, de leurs colonnes typiques, et des URI pour accéder à chaque table via le Content Provider.

## Emplacement de la Base de Données

La base de données `mmssms.db` est généralement stockée à l'emplacement suivant sur un appareil Android :

```
/data/data/com.android.providers.telephony/databases/mmssms.db
```

L'accès à cette base de données nécessite des privilèges root sur l'appareil Android.

## Tables et URI

### 1. **Table `sms`**
Cette table stocke les messages SMS.

- **URI d'accès :** `content://sms/`

| Colonne               | Type     | Description                                                                                     |
|-----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                 | INTEGER  | Identifiant unique pour chaque SMS.                                                             |
| `thread_id`           | INTEGER  | Identifiant du fil de discussion auquel ce message appartient.                                  |
| `address`             | TEXT     | Numéro de téléphone de l'expéditeur ou du destinataire.                                         |
| `person`              | INTEGER  | Identifiant de la personne (contact) associée à ce numéro.                                      |
| `date`                | INTEGER  | Horodatage de l'envoi/réception du message (en millisecondes depuis epoch).                     |
| `date_sent`           | INTEGER  | Horodatage de l'envoi réel du message (utilisé principalement pour les messages envoyés).       |
| `protocol`            | INTEGER  | Protocole de message (0 = SMS, 1 = MMS).                                                        |
| `read`                | INTEGER  | Statut de lecture (1 = lu, 0 = non lu).                                                         |
| `status`              | INTEGER  | Statut du message (0 = aucun, 64 = envoi en cours, 128 = envoyé, 32 = en échec).                |
| `type`                | INTEGER  | Type de message (1 = boîte de réception, 2 = envoyé, etc.).                                     |
| `reply_path_present`  | INTEGER  | Indicateur si le chemin de réponse est présent.                                                 |
| `subject`             | TEXT     | Sujet du message (souvent vide pour les SMS).                                                   |
| `body`                | TEXT     | Contenu du message texte.                                                                       |
| `service_center`      | TEXT     | Centre de service qui a géré le message.                                                        |
| `locked`              | INTEGER  | Statut de verrouillage du message (1 = verrouillé, 0 = non verrouillé).                         |
| `error_code`          | INTEGER  | Code d'erreur en cas d'échec d'envoi.                                                           |
| `seen`                | INTEGER  | Indicateur si le message a été vu (1 = vu, 0 = non vu).                                         |
| `sub_id`              | INTEGER  | Identifiant de l'abonnement SIM utilisé pour envoyer/recevoir ce message.                       |
| `creator`             | TEXT     | Paquet de l'application qui a créé le message.                                                  |

### 2. **Table `threads`**
Cette table contient des informations sur les fils de discussion (regroupant les messages par conversation).

- **URI d'accès :** `content://sms/conversations/`

| Colonne                | Type     | Description                                                                                     |
|------------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                  | INTEGER  | Identifiant unique pour chaque fil de discussion.                                               |
| `date`                 | INTEGER  | Horodatage du dernier message dans le fil.                                                      |
| `message_count`        | INTEGER  | Nombre total de messages dans ce fil.                                                           |
| `recipient_ids`        | TEXT     | Liste des identifiants des destinataires dans le fil.                                           |
| `snippet`              | TEXT     | Un extrait du dernier message du fil.                                                           |
| `snippet_cs`           | INTEGER  | Ensemble de caractères utilisés dans l'extrait.                                                 |
| `read`                 | INTEGER  | Statut de lecture de tous les messages du fil (1 = lu, 0 = non lu).                             |
| `archived`             | INTEGER  | Indicateur si le fil est archivé.                                                               |
| `type`                 | INTEGER  | Type de fil (par exemple, 1 = SMS, 2 = MMS, etc.).                                              |
| `error`                | INTEGER  | Code d'erreur si le fil contient un message en échec.                                           |
| `has_attachment`       | INTEGER  | Indicateur si le fil contient des pièces jointes (1 = oui, 0 = non).                            |
| `status`               | INTEGER  | Statut global du fil.                                                                           |
| `read_count`           | INTEGER  | Nombre de messages lus dans le fil.                                                             |
| `unread_count`         | INTEGER  | Nombre de messages non lus dans le fil.                                                         |
| `has_drm`              | INTEGER  | Indicateur si le fil contient des messages avec DRM.                                            |
| `seen`                 | INTEGER  | Indicateur si le fil a été vu (1 = vu, 0 = non vu).                                             |

### 3. **Table `mms`**
Cette table contient les messages MMS.

- **URI d'accès :** `content://mms/`

| Colonne                  | Type     | Description                                                                                     |
|--------------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                    | INTEGER  | Identifiant unique pour chaque MMS.                                                             |
| `thread_id`              | INTEGER  | Identifiant du fil de discussion auquel ce message appartient.                                  |
| `date`                   | INTEGER  | Horodatage de l'envoi/réception du message.                                                     |
| `date_sent`              | INTEGER  | Horodatage de l'envoi réel du message.                                                          |
| `msg_box`                | INTEGER  | Boîte de message (1 = réception, 2 = envoi, etc.).                                              |
| `read`                   | INTEGER  | Statut de lecture du MMS (1 = lu, 0 = non lu).                                                  |
| `m_id`                   | TEXT     | Identifiant du message MMS.                                                                     |
| `sub`                    | TEXT     | Sujet du message MMS.                                                                           |
| `sub_cs`                 | INTEGER  | Ensemble de caractères du sujet.                                                                |
| `ct_t`                   | TEXT     | Type de contenu du message.                                                                     |
| `ct_l`                   | TEXT     | Localisation du contenu.                                                                        |
| `exp`                    | INTEGER  | Expiration du message.                                                                          |
| `m_size`                 | INTEGER  | Taille du message.                                                                              |
| `pri`                    | INTEGER  | Priorité du message.                                                                            |
| `rr`                     | INTEGER  | Indicateur de demande de rapport de lecture.                                                    |
| `resp_st`                | INTEGER  | Statut de la réponse.                                                                           |
| `st`                     | INTEGER  | Statut de la transaction.                                                                       |
| `tr_id`                  | TEXT     | Identifiant de la transaction.                                                                  |
| `retr_st`                | INTEGER  | Statut de la récupération du message.                                                           |
| `retr_txt`               | TEXT     | Texte de la récupération du message.                                                            |
| `retr_txt_cs`            | INTEGER  | Ensemble de caractères du texte de récupération.                                                |
| `read_status`            | INTEGER  | Statut de lecture du MMS.                                                                       |
| `ct_cls`                 | TEXT     | Classe du contenu.                                                                              |
| `resp_txt`               | TEXT     | Texte de la réponse.                                                                            |
| `d_rpt`                  | INTEGER  | Demande de rapport de livraison.                                                                |
| `locked`                 | INTEGER  | Statut de verrouillage du message (1 = verrouillé, 0 = non verrouillé).                         |
| `sub_id`                 | INTEGER  | Identifiant de l'abonnement SIM utilisé pour envoyer/recevoir ce message.                       |
| `creator`                | TEXT     | Paquet de l'application qui a créé le message.                                                  |

### 4. **Table `part` (pour MMS)**
Cette table stocke les différentes parties d'un message MMS, comme les images, le texte, etc.

- **URI d'accès :** `content://mms/part/`

| Colonne              | Type     | Description                                                                                     |
|----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                | INTEGER  | Identifiant unique pour chaque partie.                                                          |
| `mid`                | INTEGER  | Identifiant du message MMS auquel cette partie appartient.                                      |
| `seq`                | INTEGER  | Numéro de séquence de la partie.                                                                |
| `ct`                 | TEXT     | Type MIME de la partie (par exemple, `image/jpeg`, `text/plain`, etc.).                         |
| `name`               | TEXT     | Nom du fichier si c'est une pièce jointe.                                                       |
| `chset`              | INTEGER  | Jeu de caractères utilisé si c'est du texte.                                                    |
| `cd`                 | TEXT     | Disposition de la partie (par exemple, `attachment`, `inline`).                                 |
| `fn`                 | TEXT     | Nom de fichier original.                                                                        |
| `

data`               | BLOB     | Données de la partie (contenu réel pour les images, fichiers, etc.).                            |
| `text`               | TEXT     | Texte associé à la partie si c'est du texte.                                                    |
| `size`               | INTEGER  | Taille de la partie en octets.                                                                  |


### 5. **Table `cbs`**
Cette table stocke les messages CBS (Cell Broadcast Service), souvent utilisés pour diffuser des messages d'information ou d'urgence à tous les téléphones dans une zone spécifique.

- **URI d'accès :** `content://cbs/`

| Colonne               | Type     | Description                                                                                     |
|-----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                 | INTEGER  | Identifiant unique pour chaque message CBS.                                                     |
| `message_id`          | INTEGER  | Identifiant du message.                                                                        |
| `broadcast_id`        | INTEGER  | Identifiant de la diffusion.                                                                    |
| `plmn`                | TEXT     | Code PLMN (Public Land Mobile Network) de l'opérateur de diffusion.                            |
| `language`            | TEXT     | Langue du message.                                                                            |
| `body`                | TEXT     | Contenu du message CBS.                                                                        |
| `date`                | INTEGER  | Horodatage de la réception du message (en millisecondes depuis epoch).                         |
| `received_time`       | INTEGER  | Horodatage spécifique à la réception du message.                                                |

### 6. **Table `detailed_sms`**
Cette table contient des informations détaillées sur les SMS, souvent utilisées pour des analyses ou des fonctions spécifiques des applications.

- **URI d'accès :** `content://sms/detailed/`

| Colonne               | Type     | Description                                                                                     |
|-----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                 | INTEGER  | Identifiant unique pour chaque SMS détaillé.                                                    |
| `thread_id`           | INTEGER  | Identifiant du fil de discussion auquel ce message appartient.                                  |
| `address`             | TEXT     | Numéro de téléphone de l'expéditeur ou du destinataire.                                         |
| `person`              | INTEGER  | Identifiant de la personne (contact) associée à ce numéro.                                      |
| `date`                | INTEGER  | Horodatage de l'envoi/réception du message (en millisecondes depuis epoch).                     |
| `body`                | TEXT     | Contenu du message texte.                                                                       |
| `status`              | INTEGER  | Statut du message (0 = aucun, 64 = envoi en cours, 128 = envoyé, 32 = en échec).                |
| `protocol`            | INTEGER  | Protocole de message (0 = SMS, 1 = MMS).                                                        |
| `error_code`          | INTEGER  | Code d'erreur en cas d'échec d'envoi.                                                           |

### 7. **Table `sms_inbox`**
Cette table contient uniquement les messages SMS entrants, souvent utilisés pour les fonctions de filtrage ou d'analyse des messages entrants.

- **URI d'accès :** `content://sms/inbox/`

| Colonne               | Type     | Description                                                                                     |
|-----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                 | INTEGER  | Identifiant unique pour chaque message SMS entrant.                                             |
| `thread_id`           | INTEGER  | Identifiant du fil de discussion auquel ce message appartient.                                  |
| `address`             | TEXT     | Numéro de téléphone de l'expéditeur.                                                            |
| `person`              | INTEGER  | Identifiant de la personne (contact) associée à ce numéro.                                      |
| `date`                | INTEGER  | Horodatage de la réception du message (en millisecondes depuis epoch).                         |
| `body`                | TEXT     | Contenu du message texte.                                                                       |
| `status`              | INTEGER  | Statut du message (0 = aucun, 64 = envoi en cours, 128 = envoyé, 32 = en échec).                |
| `read`                | INTEGER  | Statut de lecture (1 = lu, 0 = non lu).                                                         |

### 8. **Table `sms_sent`**
Cette table contient uniquement les messages SMS envoyés, souvent utilisés pour les fonctions de gestion des messages sortants.

- **URI d'accès :** `content://sms/sent/`

| Colonne               | Type     | Description                                                                                     |
|-----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                 | INTEGER  | Identifiant unique pour chaque message SMS envoyé.                                             |
| `thread_id`           | INTEGER  | Identifiant du fil de discussion auquel ce message appartient.                                  |
| `address`             | TEXT     | Numéro de téléphone du destinataire.                                                            |
| `date`                | INTEGER  | Horodatage de l'envoi du message (en millisecondes depuis epoch).                              |
| `body`                | TEXT     | Contenu du message texte.                                                                       |
| `status`              | INTEGER  | Statut du message (0 = aucun, 64 = envoi en cours, 128 = envoyé, 32 = en échec).                |
| `read`                | INTEGER  | Statut de lecture (1 = lu, 0 = non lu).                                                         |

### 9. **Table `mms_part`**
Cette table est une extension de `mms`, contenant des informations spécifiques sur les parties individuelles des messages MMS.

- **URI d'accès :** `content://mms/part/`

| Colonne              | Type     | Description                                                                                     |
|----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                | INTEGER  | Identifiant unique pour chaque partie de MMS.                                                   |
| `mid`                | INTEGER  | Identifiant du message MMS auquel cette partie appartient.                                      |
| `seq`                | INTEGER  | Numéro de séquence de la partie.                                                                |
| `ct`                 | TEXT     | Type MIME de la partie (par exemple, `image/jpeg`, `text/plain`, etc.).                         |
| `name`               | TEXT     | Nom du fichier si c'est une pièce jointe.                                                       |
| `chset`              | INTEGER  | Jeu de caractères utilisé si c'est du texte.                                                    |
| `data`               | BLOB     | Données de la partie (contenu réel pour les images, fichiers, etc.).                            |

### 10. **Table `mms_sms`**
Cette table contient les messages MMS qui ont été convertis en SMS pour être stockés dans le format SMS.

- **URI d'accès :** `content://mms/sms/`

| Colonne               | Type     | Description                                                                                     |
|-----------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`                 | INTEGER  | Identifiant unique pour chaque message MMS converti en SMS.                                      |
| `thread_id`           | INTEGER  | Identifiant du fil de discussion auquel ce message appartient.                                  |
| `address`             | TEXT     | Numéro de téléphone du destinataire.                                                            |
| `date`                | INTEGER  | Horodatage de la conversion du message (en millisecondes depuis epoch).                         |
| `body`                | TEXT     | Contenu du message texte.                                                                       |

---
