<div class="pgnt"><#if !page.firstPage>
    <a href="${baseUrl}">??</a><#else>
    ??</#if><#if page.maxpno gte 3><#list (page.pno-2>2)?then(page.pno-2,2)..(page.pno+2 lt page.maxpno)?then(page.pno+2,page.maxpno-1) as i>
    <span><#if page.pno=i>?${i}?<#else><a href="${baseUrl+'?page='+i}">?${i}?</a></#if></span></#list></#if>
    <span><#if page.maxpno gte 2><#if !page.lastPage><a href="${baseUrl+'?page='+page.maxpno}">??</a><#else>
        ??</#if></#if>
    </span><span>??${page.maxpno}?</span>
</div>