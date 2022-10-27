http://localhost:8080/api/products

 HTTP/1.1
content-type: application/json
'{
    "nome": "coca-cola",
    "descricao": "coca-cola 600ml",
    "preco": 12.50
}'


curl -X POST -H "Content-Type: application/json" -d '{ "nome": "coca-cola",  "descricao": "coca-cola 600ml",  "preco": 12.50 }' http://localhost:8080/api/products
    
