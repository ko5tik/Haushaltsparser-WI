package de.pribluda.pirate.barser;

import com.mongodb.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.SortTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        siteGenerator.createLeaf("41 Kulturamt");
    }


    public void createLeaf(String entity) throws IOException {

        final DBObject query = new BasicDBObject();
        query.put("_id", entity);

        final DBObject entry = titles.findOne(query);

        if (entity == null) {
            System.err.println("no entry  found for id: '" + entity + "'");
            return;
        }

        // ok, entry found create context

        final VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("entity", entry);
        velocityContext.put("sorter", new SortTool());

        // create writer
        final FileWriter fileWriter = new FileWriter(destinationPath + File.separator + entity + ".html");

        // merge
        velocityEngine.mergeTemplate("/site/leafEntry.vm", "UTF-8", velocityContext, fileWriter);

        fileWriter.flush();
        fileWriter.close();
    }
}
