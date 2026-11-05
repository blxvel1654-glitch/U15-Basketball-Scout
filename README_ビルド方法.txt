U15 Basketball Scout Androidプロジェクト

重要:
このZIPはAndroid StudioでAPKをビルドするためのプロジェクト一式です。
この実行環境にはAndroid SDK/Gradleがないため、APK自体のコンパイルは行えていません。

APKの作成:
1. PCにAndroid Studioをインストール
2. Android Studioでこのフォルダ U15_Basketball_Scout_Android を開く
3. SDK Platform 35 と必要なBuild Toolsをインストール
4. Gradle同期が完了したら Build > Build Bundle(s) / APK(s) > Build APK(s)
5. app/build/outputs/apk/debug/app-debug.apk が生成されます

機能:
- HTML版の試合記録、選手別スタッツ、相手分析、対策候補、交代・出場時間
- カメラ起動と撮影
- localStorageによる端末内保存
- CSV/JSON等の書き出しはアプリ専用exportsフォルダに保存

注意:
- カメラ初回起動時にAndroidのカメラ権限を許可してください。
- 試合データはアプリ内WebViewに保存されます。アプリのデータ削除・アンインストール前にバックアップしてください。
- 静止画やエクスポートファイルはアプリ専用フォルダに保存されます。
