#ak=xxxx
#pk=xxxx
host='bcs.duapp.com'

method='GET'
bucket='bucketName'
object='/"objectName"'


c='MBO'+'\n'+\
'Method='+method+'\n'+\
'Bucket='+bucket+'\n'+\
'Object='+object+'\n'

from urllib.parse import quote
from hashlib import sha1
from hmac import new
from base64 import b64encode

h=new(pk.encode(),b'',sha1)
h.update(c.encode())
s=h.digest()
s2=quote(b64encode(s))

sign='MBO:'+ak+':'+s2

url='http://'+host+'/'+bucket+object+'?sign='+sign
