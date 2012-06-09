package de.pribluda.pirate.barser;

import com.mongodb.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.SortTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Generate website from  database  using velocity
 *
 * @author Konstantin Pribluda
 */
public class SiteGenerator {


    final String database;
    final String destinationPath;
    private final VelocityEngine velocityEngine;
    private final Mongo mongo;
    private final DB db;
    private final DBCollection titles;
    private final DBCollection budget;
    private final VelocityContext velocityContext;
    private final NameNormaliser normaliser;

    public SiteGenerator(String destinationPath, String database) throws IOException {
        this.destinationPath = destinationPath;
        this.database = database;

        final Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("velocity.properties"));
        velocityEngine = new VelocityEngine(properties);
        velocityEngine.init();

        mongo = new Mongo();
        db = mongo.getDB(database);

        titles = db.getCollection("graph");
        budget = db.getCollection("budget");


        velocityContext = new VelocityContext();
        velocityContext.put("sorter", new SortTool());
        velocityContext.put("number", new NumberTool());
        normaliser = new NameNormaliser();
        velocityContext.put("name", normaliser);
    }



    /**
     * Usage:
     * SiteGenerator [destdir]
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("usage: java SiteGenerator.class <destination path>  <mongo database connection>");
        }

        final SiteGenerator siteGenerator = new SiteGenerator(args[0], args[1]);

        siteGenerator.createStartPage();
    }

    /**
     * create start page / just iterate over top entities
     */
    public void createStartPage() throws IOException {

        //  top level entities have no parents
        final BasicDBObject query = new BasicDBObject();
        query.put("parent",new BasicDBObject("$exists", false));

        final List parentEntities = budget.distinct("Entity", query);
        velocityContext.put("entities",parentEntities);

        // entities with parents
        query.put("parent",new BasicDBObject("$exists", true));
        List silbings = budget.distinct("Entity", query);

        // also show structure
        final BasicDBObject structureQuery = new BasicDBObject();
        final DBCollection structure = db.getCollection("structure");
        velocityContext.put("structure",structure);


        mergeTemplate("site/start.vm", velocityContext, destinationPath + File.separator + "index.html");

        // iterate over the parents and create their pages
        for(Object parentTitle : parentEntities) {
            createParentPage(parentTitle.toString());
        }

        //  walk throught structure and create leaf entries

        for(DBObject o: structure.find()) {
            String dezernat = String.valueOf(o.get("_id"));
            final DBObject value = (DBObject) o.get("value");

            System.err.println("dezernat: " + dezernat);
            final DBObject aemte = (DBObject) value.get("aemte");
            for(String amt : aemte.keySet()) {
                final DBObject kostenstellen = (DBObject) aemte.get(amt);
                for(String kostenstelle: kostenstellen.keySet()) {
                    System.err.println( dezernat + "/"  + amt + "/"  + kostenstelle);
                    createLeaf(dezernat,amt,kostenstelle);
                }
            }

        }
    }

    /**
     * render parent page
     * @param title
     */
    private void createParentPage(String title) throws IOException {

        final DBObject entry = retrieveGraphEntity(title);


        if (entry == null) {
            System.err.println("no entry  found for id: '" + title + "'");
            return;
        }

        // ok, entry found create context
        velocityContext.put("entity", entry);

        final BasicDBObject query = new BasicDBObject();
        query.put("parent",title);

        final List silbings = budget.distinct("Entity", query);

        System.err.println("silbings:" + silbings);
        velocityContext.put("siblings", silbings);

        // first rnder parent page itself
        System.err.println("title: " + title);

        final String fileName = destinationPath + File.separator + normaliser.normalize(title) + ".html";

        mergeTemplate("/site/parentEntry.vm", velocityContext, fileName);



    }

    public void createLeaf(String parent,  String amt, String entity) throws IOException {

        final DBObject entry = retrieveGraphEntity(entity);

        if (entity == null) {
            System.err.println("no entry  found for id: '" + entity + "'");
            return;
        }

        System.err.println("entity:" + entity);
        // ok, entry found create context
        velocityContext.put("entity", entry);

        final StringBuffer fileName =  new StringBuffer();
        fileName.append(destinationPath).append(File.separator)
                .append(parent).append(File.separator)
                .append(amt).append(File.separator)
                .append(entity).append(".html");

        //System.out.println(fileName);
        mergeTemplate("/site/leafEntry.vm", velocityContext, fileName.toString());


    }

    /**
     * retrieve graph entity by name
     * @param entity
     * @return
     */
    private DBObject retrieveGraphEntity(String entity) {
        final DBObject query = new BasicDBObject();
        query.put("_id", entity);

        return titles.findOne(query);
    }

    private void mergeTemplate(String templateName, VelocityContext velocityContext1, String fileName) throws IOException {
        final File file = new File(fileName);
        file.getParentFile().mkdirs();
        // create writer
        final FileWriter fileWriter = new FileWriter(file);

        // merge
        velocityEngine.mergeTemplate(templateName, "UTF-8", velocityContext1, fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }
}
