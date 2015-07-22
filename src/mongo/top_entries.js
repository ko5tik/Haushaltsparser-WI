/**
 retrieve top level entries from database  top level entities have no parent defined
 */

db.topLevel.drop()

var topLevel = db.budget.distinct("Entity", { "parent":{$exists:false}});

db.topLevel.insert({"titles":topLevel})




