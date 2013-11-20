It's a mirror use of the "duapp.com", baidu cloud hosted web app, with maven build.
Users have to register at "duapp.com" and hold a private application/secret key first.
some client jars are already listed under "dependency"
 for useful services that the cloud host of "duapp.com" provides.
Such as mongodb, redis, mysql etc.

clean install -Dappid={baidu cloud host application key} -Dversion={baidu cloud host svn version num}
would compile then upload current webapp using properties.
Alternatively, move the 2 properties, application key and svn host version num, under the configuration element of "install" phrase makes more convenient.

Replace the local pass store
 especially since a wrong initial login:
search pom.xml and update contents of
"changepass"
element to true, for a login with every "install" command.
