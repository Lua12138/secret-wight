<!DOCTYPE html>
<html>
<head>
<title>Categories</title>
</head>
<body>
<h2>Categories</h2>
<ul>
<c:for item="${categories}" var="ct">
<li>
<span>${ct.id}</span>
<span>${ct.name}</span>
</li>
</c:for>
</ul>
</body>
</html>