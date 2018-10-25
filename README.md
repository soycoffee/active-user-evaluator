# Overview

Backlog ユーザーの過去 N 日間の活動を集計し、活動が多いユーザーから順に出力する。  
出力先は Typetalk の webhook とする。  
目的は各ユーザーの作業モチベーションアップとする。  

# 実行環境

* Scala 2.12
* Play Framework 2.6

# API

* [Backlog API](https://developer.nulab-inc.com/ja/docs/backlog/)
* [Typetalk API](https://developer.nulab-inc.com/ja/docs/typetalk/)

# 他に利用する技術

* PostgreSQL: APIキーなどの保存
* Slick: データベースの操作
* ScalaTest: ユニットテスト
* Mockito: テストモック
* Heroku: デプロイ先

# データベース構成

conf/evolutions 参照

# HOW TO

1. Backlog のドメイン、 API キーを本システムに登録する。（管理者の操作が必要）  
  本システムの API キーが発行される。

1. Typetalk からボットを作成する。  
  Typetalk Webhook ボット作成 の項を参照する。

1. 登録したボットにメンションを送る。（メッセージの内容は不要）  
  レスポンスとしてメッセージが投稿される。

1. デフォルトでは活動の多い順から3人、過去7日間の結果を出力する。  
  別の結果にしたい場合、 Typetalk Webhook オプション の項を利用する。

# Typetalk Webhook ボット作成

1. topic.post を許可する。

1. Webhook を有効化する。

1. URLを登録する。 Typetalk Webhook URL の項にあるものを使う。

1. メンションのみ送信するように設定する。

# Typetalk Webhook URL

`:projectKey` は対象のプロジェクトを指定する。  
クエリストリングに `apiKey` が必要になる。  

ベース URL: `https://active-user-evaluator.herokuapp.com`

|概要|パス|
|----|---|
|マネジメント系アクティビティ ( 課題 / マイルストーン )|`/projects/:projectKey/evaluation/management/typetalk`|
|ドキュメント系アクティビティ ( Wiki / ファイル )|`/projects/:projectKey/evaluation/document/typetalk`|
|実装系アクティビティ ( Git )|`/projects/:projectKey/evaluation/implement/typetalk`|
|上記全て|`/projects/:projectKey/evaluation/all/typetalk`|

# Typetalk Webhook オプション

メンションする際のメッセージに `count=1 sinceBeforeDays=2` といった形式で入力する。

* count: 出力するユーザー数
* sinceBeforeDays: 集計に含める日数（過去 N 日から今日まで）
