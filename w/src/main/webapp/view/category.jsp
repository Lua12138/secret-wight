<!DOCTYPE html>
<html>
<head>
<title>Category</title>
</head>
<body>
<h2>${category.name}</h2>
<ul>
<c:for item="${category.articles}" var="ar">
<li>
<span>${ar.id}</span>
<span>${ar.title}</span>
</li>
</c:for>
</ul>
</body>
</html>