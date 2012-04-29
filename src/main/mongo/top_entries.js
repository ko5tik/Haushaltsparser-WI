/**
 retrieve top level entries from database
 */


var topLevel = db.budget.distinct("Entity", { "parent":{$exists:false}});

printjson(topLevel)


