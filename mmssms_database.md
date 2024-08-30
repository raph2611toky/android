La base de données `mmssms.db` utilisée par l'application Messages sur Android contient plusieurs tables importantes, principalement pour stocker les messages SMS, MMS, et les informations connexes. Voici un aperçu des tables principales et de leurs colonnes typiques.

### 1. **Table `sms`**
Cette table stocke les messages SMS.

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
| `cid`                | TEXT     | Content-ID (pour les images inline par exemple).                                                |
| `cl`                 | TEXT     | Content-Location.                                                                               |
| `ctt_s`              | TEXT     | Type de transfert de contenu (peut indiquer si c'est un texte ou autre).                        |
| `text`               | TEXT     | Texte de la partie (pour les parties texte).                                                    |
| `data`               | BLOB     | Données binaires (image, vidéo, son, etc.).                                                     |

### 5. **Table `addr` (pour MMS)**
Cette table contient les adresses associées à chaque MMS, comme les destinataires ou l'expéditeur.

| Colonne           | Type     | Description                                                                                     |
|-------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`             | INTEGER  | Identifiant unique pour chaque adresse.                                                         |
| `msg_id`          | INTEGER  | Identifiant du message MMS auquel cette adresse est associée.                                   |
| `contact_id`      | INTEGER  | Identifiant du contact dans le carnet d'adresses (souvent inutilisé).                           |
| `address`         | TEXT     | Adresse du destinataire ou de l'expéditeur (généralement un numéro de téléphone).               |
| `type`            | INTEGER  | Type de l'adresse (par exemple, 137 pour `to`, 151 pour `cc`, 130 pour `from`, 129 pour `bcc`). |
| `charset`         | INTEGER  | Jeu de caractères utilisé pour l'adresse (peut être nul).                                       |

### 6. **Table `canonical_addresses`**
Cette table est utilisée pour stocker des adresses uniques (numéros de téléphone ou adresses e-mail) et réduire les redondances.

| Colonne           | Type     | Description                                                                                     |
|-------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`             | INTEGER  | Identifiant unique pour chaque adresse canonique.                                               |
| `address`         | TEXT     | Adresse unique (numéro de téléphone ou adresse e-mail).                                          |

### 7. **Table `sr_pending`**
Cette table est utilisée pour gérer les rapports d'état des messages (statut de livraison des MMS, par exemple).

| Colonne           | Type     | Description                                                                                     |
|-------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`             | INTEGER  | Identifiant unique pour chaque entrée de rapport d'état.                                        |
| `proto_type`      | INTEGER  | Type de protocole (souvent 1 pour MMS).                                                         |
| `msg_id`          | INTEGER  | Identifiant du message associé à ce rapport d'état.                                             |
| `error_code`      | INTEGER  | Code d'erreur associé au rapport d'état.                                                        |
| `date`            | INTEGER  | Date associée au rapport d'état.                                                                |

### 8. **Table `draft`**
Cette table peut être utilisée pour stocker des messages brouillons, bien que son utilisation soit rare comparée aux autres tables.

| Colonne           | Type     | Description                                                                                     |
|-------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`             | INTEGER  | Identifiant unique pour chaque brouillon.                                                       |
| `thread_id`       | INTEGER  | Identifiant du fil de discussion auquel ce brouillon appartient.                                |
| `message_body`    | TEXT     | Contenu du message de brouillon.                                                                |
| `date`            | INTEGER  | Horodatage de la création du brouillon.                                                         |

### 9. **Table `attachments`**
Cette table peut référencer les pièces jointes des messages, notamment pour les MMS.

| Colonne           | Type     | Description                                                                                     |
|-------------------|----------|-------------------------------------------------------------------------------------------------|
| `_id`             | INTEGER  | Identifiant unique pour chaque pièce jointe.                                                    |
| `msg_id`          | INTEGER  | Identifiant du message auquel la pièce jointe est associée.                                     |
| `content_type`    | TEXT     | Type MIME de la pièce jointe (par exemple, `image/jpeg`, `audio/mpeg`, etc.).                   |
| `filename`        | TEXT     | Nom du fichier de la pièce jointe.                                                              |
| `data`            | BLOB     | Données binaires de la pièce jointe.                                                            |

Ces tables constituent une partie essentielle de la base de données de messages sur un appareil Android, permettant de stocker, organiser et gérer les SMS et MMS de manière structurée. Ces informations sont principalement accessibles aux applications système ou avec des privilèges root en raison des restrictions de sécurité d'Android.`