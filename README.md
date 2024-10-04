# SpringBatch4Template

## バッチの起動方法
bootJarタスクで生成したjarファイルをjavaコマンドで実行する

```
java -Dfile.encoding=UTF-8 -jar SpringBatch4Template-0.0.1-SNAPSHOT.jar --spring.batch.job.enabled=true --spring.batch.job.names=HelloWorldJob
```
```
java -Dfile.encoding=UTF-8 -jar SpringBatch4Template-0.0.1-SNAPSHOT.jar --spring.batch.job.enabled=true --spring.batch.job.names=timecardJob name=sato
```

## サンプル

### HelloWorldJob

HelloWorldを出力するだけ。1Job1Step。

### TimecardJob

JobParameterで受け取ったnameをDBのテーブルに登録・表示するJob。
1:登録、2:表示の2Stepで構成している。

### TableCopyJob

### GateJob


## その他

PowerShellやGit Bashで実行すると日本語が文字化けしてしまう。

→以下のどちらかで文字化けが解消するかもしれない。

* javaの後に「-Dfile.encoding=UTF-8」オプションを付ける。（PowerShellなら""で囲む必要がある。）
* 環境変数「JAVA_TOOL_OPTIONS」に「-Dfile.encoding=UTF-8」を設定する。
