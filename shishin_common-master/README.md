#shishin_common
java用のshishin接続クラスです。バグや改善の要求は@_hakanai or @i_shogo03などなどよろしくお願いします。

##1.含まれているソースコード  
1.1.ShishinCommonClass.java  
    サーバー接続用のクラスです。基本的に編集する必要はありません。  
1.2.main.java  
    ShishinCommonClassを利用するためのサンプルソースコードです。  

##2.使い方  
2.1.ShishinCommonClass.javaを自分のプロジェクトのsrc内にコピーするか、このプロジェクトごとEclipseにインポートします。  
2.2.自分のソースコード内でShishinCommonClassをインスタンス化します。ここでインスタンスはSCClassとします。  
    ShishinCommonClass SCClass = new ShishinCommonClass(userName,serverAddress,serverPort);  
2.3.インスタンスを利用してログインします。  
    SCClass.Login();  

##3.ShishinCommonClassについて  
3.1.コンストラクタは以下の通りです。  
    ShishinCommonClass(String name,String address)  
    ShishinCommonClass(String name,String address,int port)  
  ポートの指定無しでコンストラクタが呼び出された場合、四神のデフォルトポートである13306番が設定されます。  

3.2.以下の関数が現在実装されています。  

    Boolean Login()  
      四神サーバーに自動でログインし、ゲーム開始可能になるまで待機します。ログインに失敗した場合Falseが返されます。  

    Boolean Logout()  
      四神サーバーからログアウトする処理を行います。この処理はサーバー側にログアウトのコマンドを一方的に送信するため完全なログアウトを保証するものではありません。  

    Boolean  IsFirst()  
      先攻か後攻かを判別します。先攻の場合true、後攻の場合falseが返却されます。  

    String GetData()  
      サーバーからデータを取得し、そのデータをStringで返却します。この時、Stringは配列となっています。また、返却されるString配列は取得に失敗した場合nullとなる可能性があることを考慮してください。  

    Boolean SendMsg()  
      メッセージを送信します。送信時先頭に自動で自身のログインユーザー名が付与されます。  


##4.main.java(サンプル)について  
    サンプルでは、サーバーへのログインと、サーバーからのデータ取得が実装されています。  
    エクスポートしたjarを実行する際に以下のコマンドライン引数を指定することができます。  


    -a address
      接続するアドレスをデフォルトから変更します。

    -p port
      接続するポートをデフォルトから変更します。

    -u userName
      サーバーへログインする際利用するユーザーネームをデフォルトから変更します。

    また、接続に成功した場合、コマンドを待機します。以下のコマンドを入力することができます。  

    q
      サーバーからログアウトします。
    
    c 
      cを入力後、送信したい文字列を入力することによってサーバーにチャットを送信します。

    g
      サーバーからデータを取得します。


