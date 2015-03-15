//frequency of main characters
start n=node(*)  match (p:Prediction)-[:MAIN]->(n:Number),(last:Letter) 
where (p)-[:FOR]->(last) and has(last.LATEST) 
with count((p:Prediction)-[:MAIN]->(n:Number)) as frequency,n.value as character 
return character, frequency
order by frequency desc;