//main letter
match (p:Prediction)-[:FOR]->(l:Letter),  path=(n2:Number)<-[:MAIN]-(p)
where has(l.LATEST)  
with  p,n2
order by n2.value asc
with Id(p) as wordId, collect(n2.value) as letterData, p.wordsize as wordSize, p.distance as distance
return wordId, wordSize, distance, letterData;