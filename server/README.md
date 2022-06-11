# CORS filter
- https://ofstack.com/Java/42623/spring-cloud-gateway-cross-domain-global-cors-configuration-mode.html

# CORS check
```
curl -v -H "Access-Control-Request-Method: GET" -H "Origin: http://localhost:3000" -X OPTIONS \
	http://localhost:8080/resource/data.csv
```

# Spring webflux
- https://www.slideshare.net/Pivotal/how-to-avoid-common-mistakes-when-using-reactor-netty
