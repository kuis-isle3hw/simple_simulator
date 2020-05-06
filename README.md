# simple_simulator

[計算機科学実験及演習3(HW)](http://www.lab3.kuis.kyoto-u.ac.jp/~takase/le3a/)の仕様に準拠する16bit,4096wordsのSIMPLEの命令セットシミュレータです. Javaで記述されています.  
命令レベルのシミュレーションであるため, アセンブリ記述のコードの機能のみを検証できることに注意してください. 

## 実行方法

### 1. IDE上で実行

NetBeansやEclipseなど，Java対応の任意のIDEでよしなに実行可能です．実験1等でも使用されていたかと思います．スタッフはVSCode (+Java Extension Pack) が好みのようです．  
リポジトリのclone先ディレクトリを指定してIDEで開いてください．

### 2. jarファイルで実行

ビルド済みの[SimpleSimulator.jar](SimpleSimulator.jar)を用意してあります．  
IDEを用意するのが面倒な場合は，これをダウンロードしてきて，コマンドプロンプトやターミナルで下記の通り実行することができます．

```bash
java -jar SimpleSimulator.jar [-d] [-m] [input_file] 
```

ビルドには下記のツールとバージョンを使用しています．

- [AdoptOpenJDK (build 11.0.7+10)](https://adoptopenjdk.net/)
- [Apache Ant(TM) version 1.10.6](https://ant.apache.org/)

`ant`はVSCodeのビルドタスクとして登録してあるので，よしなにしてください．


### 実行時オプション

- `[-d]` : デバッグモード. 1命令実行するごとに状態を表示します.
- `[-m]` : 入力に対応する`.mif`ファイルを生成します. ファイル名は`output.mif`となっています.
- `[input_file]` : 入力とするファイルを選択できます. 指定がない場合は同梱の`sample.txt`を入力に用います.

## 入力ファイルの仕様

各行にはデータを1つだけ記述することができます.

それぞれのデータは, 以下のいずれかの方法で書くことができます.

- 数値を(10進法で)直接記述する. 値は16bitの範囲内とする.
- SIMPLEの命令形式に従って記述する. このときアルファベットは大文字でも小文字でもよい.

例えば `0` , `-32768`, `Add 3,1`, `HLT`などがデータとして認識される文字列です.
`32768`, `0b1100110010101010`, `halt`などは不適切なデータとして無視されます.

また, `//`から行末まではコメントとみなされます. 例えば`Add 0, 1// r[0] += r[1];`のように書くことができます.

## 出力の読み方

標準出力には, 以下の情報が出力されます.
- 入力命令`IN`が読み込まれたとき, 入力を促す文字列 `Input? : `. これが表示された場合, 利用者は入力とする値を(10進法で)標準入力に与える.
- 出力命令`OUT`が読み込まれたとき, その命令の行番号および出力内容`Output on X : Y`. PCが`X`の出力命令の出力内容が`Y`であったことを表す. `X`および`Y`はともに10進法で表現される.

デバッグモード(`-d`)が有効のとき, 標準エラー出力には以下の情報が表示されます.
- 実行した命令の行番号と内容.
- 命令を実行した直後のレジスタおよび条件コードの状態. まずレジスタの状態が配列として与えられ, 次に条件コードが`S`,`Z`,`C`,`V`の順で与えられる. 例えば`[0,0,0,3,0,0,0,0],(0010)`は`r[4]`の値が`3`であり条件コード`carry`が`1`であるような状態を表す.
なおデータ数の上限は4096ですが, 入力ファイルに含まれる命令がこれ未満の場合は, 命令として実行した場合に影響を与えない値で残りが埋められます.

## 注意点など

- [MIT License](LICENSE) です．この条項下で自由にご利用ください．また，本ツールの使用にあたって何か問題が起きても，スタッフは一切の責任を負いません．
- [SIMPLEアーキテクチャの基本命令セット](http://www.lab3.kuis.kyoto-u.ac.jp/~takase/le3a/#SIMPLE)のみに対応しています．
  - この範囲内でbugなどあればぜひ [Issue](../../issues) で教えてください．
  - この範囲内で機能追加や改善があれば [Pull request](../../pulls) も歓迎します．
  - 課題でもある独自の改良や拡張については，対応しません．

