# SpringBatch4Template

## バッチの起動方法
bootJarタスクで生成したjarファイルをjavaコマンドで実行する

```
java -jar .\SpringBatch4Template-0.0.1-SNAPSHOT.jar --spring.batch.job.enabled=true --spring.batch.job.names=timecardJob name=sato
```

## サンプル

### doSomethingJob

HelloWorld的な存在。1Job1Step。

### timecardJob

JobParameterで受け取ったnameをDBのテーブルに登録・表示するJob。
1:登録、2:表示の2Stepで構成している。

## その他

PowerShellやGit Bashで実行すると日本語が文字化けしてしまう。

→環境変数：「JAVA_TOOL_OPTIONS」「-Dfile.encoding=UTF-8」を設定すると文字化けが解消する