//frequency of lucky characters 
start n=node(*)  match (p:Prediction)-[:LUCKY]->(n:Number),(last:Letter) 
where (p)-[:FOR]->(last) and has(last.LATEST) 
with count((p:Prediction)-[:LUCKY]->(n:Number)) as frequency,n.value as character 
return character, frequency 
order by frequency desc;
