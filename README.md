# Overview

Backlog ユーザーの過去 N 日間の活動を集計し、活動が多いユーザーから順位付けして出力する。  
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
