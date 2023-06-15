# Spring Boot R2DBC Rest Sample Application

## 開発環境（IntelliJ）の推奨設定

- アノテーション処理を有効化する。
  - Settings > Build, Excecution, Deployment > Compiler > Annotation Processor > `Enable Annotation Processing`をONにする。
- bootRunを実行している場合でもビルドされるようにする。（単一ファイルのビルドを実行すると spring-devtools が変更を検知して自動的に再起動されるようになります）
    - Intellij > Ctrl+Shift+A > type Registry... > `compiler.automake.allow.when.app.running`をONにする。
- Windowsの場合は、コンソール出力が文字化けするため、`C:¥Program Files¥JetBrains¥IntelliJ Idea xx.x.x¥bin`の中にある`idea64.exe.vmoptions`
  ファイルに`-Dfile.encoding=UTF-8`を追記する。

## minikube

### on MacOS

```bash
$ # starts a local Kubernetes cluster
$ minikube start --vm-driver="hyperkit"

$ # configure environment to use minikube’s Docker daemon
$ eval $(minikube -p minikube docker-env)

$ # enable a minikube addon
$ minikube addons enable ingress

$ # redis / mysql / mailhog
$ kubectl apply -f k8s
```

### on Windows10 pro

```powershell
C:\> minikube start --vm-driver="hyperv"
C:\> minikube docker-env --shell powershell | Invoke-Expression
C:\> minikube addons enable ingress
C:\> kubectl apply -f k8s
```

### set External URL

minikubeのIPアドレスを確認して、gradle.propertiesに設定する

```bash
$ minikube ip
192.168.64.3

$ vi gradle.properties
---
# set nip.io domain with minikube ip
jkube.domain=192.168.64.3.nip.io
---
```

### Build & Apply

```bash
$ # delete old service
$ ./gradlew k8sUndeploy

$ # apply new service
$ ./gradlew clean k8sApply

$ # check pod, svc
$ kubectl get all

$ # tail the log
$ ./gradlew k8sLog
```

### Check running

```bash
$ # check the ingress resource
$ kubectl get ingress
NAME                            CLASS   HOSTS                                               ADDRESS        PORTS   AGE
spring-boot-r2dbc-rest-sample   nginx   spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io   192.168.64.3   80      11m

$ # send a request
$ curl http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/actuator/health
{"status":"UP","groups":["liveness","readiness"]}
```

## 動作確認

### curlでの動作確認

```bash
$ # ログイン
$ curl -s -L -X POST 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/auth/login' \
    -H 'Content-Type: application/json' \
    --data-raw '{
    "username": "test@example.com",
    "password": "passw0rd" }' | jq .
{
  "data": {
    "accessToken": "<access-token>",
    "refreshToken": "<refresh-token>"
  },
  "success": true,
  "message": "正常終了"
}

$ # トークンリフレッシュ
$ curl -s -L -X POST 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/auth/refresh' \
    -H 'Content-Type: application/json' \
    --data-raw '{
        "accessToken": "<access-token>",
        "refreshToken": "<refresh-token>"
    }' | jq .
{
    "data": {
        "accessToken": "<new-access-token>",
        "refreshToken": "<new-refresh-token>"
    },
    "success": true,
    "message": "正常終了"
}

$ # 検索
$ curl -s -L -X GET 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/system/staffs' \
    -H 'Authorization: Bearer <access-token>' | jq .
{
  "page": 0,
  "perpage": 10,
  "count": 1,
  "totalPages": 1,
  "data": [
    {
      "createdAt": "2022-06-23T01:19:50",
      "updatedAt": "2022-06-23T01:19:50",
      "version": 1,
      "id": "23fb7d1e-f247-11ec-8bad-0242ac11000a",
      "firstName": "john",
      "lastName": "doe",
      "email": "test@example.com",
      "tel": "09011112222"
    }
  ],
  "success": true,
  "message": "正常終了"
}

$ # 登録
$ curl -s -L -X POST 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/system/staff' \
    -H 'Authorization: Bearer <access-token>' \
    -H 'Content-Type: application/json' \
    --data-raw '{
        "firstName": "jiro",
        "lastName": "yamada",
        "password": "aaaaaaaa",
        "passwordConfirm": "aaaaaaaa",
        "email": "aaaa@bbbb.com",
        "tel": "0000000000"
    }' | jq .
{
  "data": {
    "createdAt": "2022-06-24T13:19:28.273167",
    "updatedAt": "2022-06-24T13:19:28.273167",
    "version": 0,
    "id": "d312b078-64d1-492e-a5b9-0f0f390d25b9",
    "firstName": "jiro",
    "lastName": "yamada",
    "email": "aaaa@bbbb.com",
    "tel": "0000000000"
  },
  "success": true,
  "message": "正常終了"
}

$ # 更新
$ curl -s -L -X PUT 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/system/staff/fdae5e41-2edf-46ca-8b35-22cb3c5b447a' \
    -H 'Authorization: Bearer <access-token>' \
    -H 'Content-Type: application/json' \
    --data-raw '{
        "id": "fdae5e41-2edf-46ca-8b35-22cb3c5b447a",
        "firstName": "jiro2",
        "lastName": "yamada",
        "password": "aaaaaaaa",
        "passwordConfirm": "aaaaaaaa",
        "email": "aaaa@bbbb.com",
        "tel": "0000000000",
        "version": 0
    }' | jq .
{
    "data": {
        "createdAt": "2022-06-24T14:39:19",
        "updatedAt": "2022-06-24T14:48:24.435239",
        "version": 1,
        "id": "fdae5e41-2edf-46ca-8b35-22cb3c5b447a",
        "firstName": "jiro2",
        "lastName": "yamada",
        "email": "aaaa@bbbb.com",
        "tel": "0000000000"
    },
    "success": true,
    "message": "正常終了"
}

$ # 取得
$ curl -s -L -X GET 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/system/staff/fdae5e41-2edf-46ca-8b35-22cb3c5b447a' \
    -H 'Authorization: Bearer <access-token>' | jq .
{
    "data": {
        "createdAt": "2022-06-24T14:39:19",
        "updatedAt": "2022-06-24T14:48:24",
        "version": 1,
        "id": "fdae5e41-2edf-46ca-8b35-22cb3c5b447a",
        "firstName": "jiro2",
        "lastName": "yamada",
        "email": "aaaa@bbbb.com",
        "tel": "0000000000"
    },
    "success": true,
    "message": "正常終了"
}

$ # 削除
$ curl -s -L -X DELETE 'http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/api/system/staff/fdae5e41-2edf-46ca-8b35-22cb3c5b447a' \
    -H 'Authorization: Bearer <access-token>' | jq .
{
    "data": {
        "createdAt": "2022-06-24T14:39:19",
        "updatedAt": "2022-06-24T14:48:24",
        "version": 1,
        "id": "fdae5e41-2edf-46ca-8b35-22cb3c5b447a",
        "firstName": "jiro2",
        "lastName": "yamada",
        "email": "aaaa@bbbb.com",
        "tel": "0000000000"
    },
    "success": true,
    "message": "正常終了"
}
```

### Swagger UIでの動作確認

http://spring-boot-r2dbc-rest-sample.192.168.64.3.nip.io/swagger-ui.html

### データベースの確認

```bash
mysql -h 192.168.64.3 -P 30306 -uroot -ppassw0rd spring-boot-r2dbc-rest-sample

mysql> show tables;
+----------------------------------------------+
| Tables_in_spring-boot-r2dbc-rest-sample |
+----------------------------------------------+
| code_categories                              |
| codes                                        |
| flyway_schema_history                        |
| holidays                                     |
| mail_templates                               |
| permissions                                  |
| role_permissions                             |
| roles                                        |
| send_mail_queue                              |
| staff_roles                                  |
| staffs                                       |
| upload_files                                 |
| user_roles                                   |
| users                                        |
+----------------------------------------------+
14 rows in set (0.01 sec)
```

### メールの確認

http://192.168.64.3:30825

## 参考情報

| プロジェクト                                                            | 概要                               |
|:------------------------------------------------------------------|:---------------------------------|
| [JKube](https://www.eclipse.org/jkube/)                           | k8sへのデプロイを簡略化するプラグイン             |
| [Lombok Project](https://projectlombok.org/)                      | 定型的なコードを書かなくてもよくする               |
| [Springframework](https://spring.io/projects/spring-framework)    | Spring Framework（Spring WebFlux） |
| [Spring Security](https://spring.io/projects/spring-security)     | セキュリティ対策、認証・認可のフレームワーク           |
| [Project Reactor](https://projectreactor.io/)                     | リアクティブプログラミングのためのライブラリ           |
| [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) | Reactive O/Rマッパー                 |
| [SpringDoc](https://springdoc.org/)                               | OpenAPI ドキュメントツール                |
| [Flyway](https://flywaydb.org/)                                   | DBマイグレーションツール                    |
| [ModelMapper](http://modelmapper.org/)                            | Beanマッピングライブラリ                   |
| [Spock](http://spockframework.org/)                               | テストフレームワーク                       |
| [Mockito](http://site.mockito.org/)                               | モッキングフレームワーク                     |
| [MailHog](https://github.com/mailhog/MailHog)                     | ウェブベースのSMTPテスター                  |
