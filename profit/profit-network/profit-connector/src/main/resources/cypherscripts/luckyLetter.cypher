//lucky letter
match (p:Prediction)-[:FOR]->(l:Letter),  path=(n2:Number)<-[:LUCKY]-(p)
where has(l.LATEST) and (p.pattern="jordan")
with  p,n2
order by n2.value asc
with Id(p) as wordId, collect(n2.value) as letterData, p.wordsize as wordSize, p.distance as distance,p.pattern as pattern
return wordId, wordSize, distance, letterData, pattern
order by wordSize