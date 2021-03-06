//frequency of main letter
match (p:Prediction)-[:FOR]->(l:Letter),  path=(n2:Number)<-[:MAIN]-(p)
where has(l.LATEST) and (p.pattern="jordan")
with  p,n2
order by n2.value asc
with  collect(n2.value) as letterData, p
return letterData, count(letterData) as frequency
order by count(letterData)desc;