package ch.ethz.lapis;

import ch.ethz.lapis.core.DatabaseService;
import ch.ethz.lapis.core.GlobalProxyManager;
import ch.ethz.lapis.core.SubProgram;
import ch.ethz.lapis.source.covlineages.CovLineagesService;
import ch.ethz.lapis.source.gisaid.GisaidService;
import ch.ethz.lapis.source.ng.NextstrainGenbankService;
import ch.ethz.lapis.source.s3c.S3CVineyardService;
import ch.ethz.lapis.transform.TransformService;
import ch.ethz.lapis.util.StopWatch;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
@EnableScheduling
public class LapisMain extends SubProgram<LapisConfig> {

    public static LapisConfig globalConfig;
    public static ComboPooledDataSource dbPool;

    public LapisMain() {
        super("Lapis", LapisConfig.class);
    }

    private void test0() throws Exception {
        int size = 10000000;
        Integer[] data0 = new Integer[size];
        int[] data1 = new int[size];
        for (int i = 0; i < size; i++) {
            int value = (int) (Math.random() * 5000);
            data0[i] = value;
            data1[i] = value;
        }

        for (int j = 0; j < 20; j++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("Query data0");
            int result0 = 0;
            for (int i = 0; i < size; i++) {
                if (data0[i] > 3000) result0++;
            }
            stopWatch.round("Query data1");
            int result1 = 0;
            for (int i = 0; i < size; i++) {
                if (data1[i] > 3000) result1++;
            }
            stopWatch.stop();
            if (result0 != result1) {
                System.err.println("Wrong result!!");
                return;
            }
            System.out.println("Result: " + result0);
            System.out.println(stopWatch.getFormattedResultString());
        }
    }

    private void test1() throws Exception {
        int size = 10000000;
        String[] data0 = new String[size];
        int[] data1 = new int[size];
        for (int i = 0; i < size; i++) {
            int value = (int) (Math.random() * 5000);
            data0[i] = ("string" + value).intern();
            data1[i] = value;
        }

        for (int j = 0; j < 20; j++) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("Query data0");
            int result0 = 0;
            for (int i = 0; i < size; i++) {
                // Because everything is interned, it might also be safe to use == instead of equals()
                if (data0[i].equals("string3000")) result0++;
            }
            stopWatch.round("Query data1");
            int result1 = 0;
            for (int i = 0; i < size; i++) {
                if (data1[i] == 3000) result1++;
            }
            stopWatch.stop();
            if (result0 != result1) {
                System.err.println("Wrong result!!");
                return;
            }
            System.out.println("Result: " + result0);
            System.out.println(stopWatch.getFormattedResultString());
        }
    }

    @Override
    public void run(String[] args, LapisConfig config) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("TODO: write help page");
        }
        globalConfig = config;
        dbPool = DatabaseService.createDatabaseConnectionPool(LapisMain.globalConfig.getVineyard());
        GlobalProxyManager.setProxyFromConfig(config.getHttpProxy());

//        test0();
        test1();
        System.exit(0);

        if ("--api".equals(args[0])) {
            String[] argsForSpring = Arrays.copyOfRange(args, 1, args.length);
            System.setProperty("spring.mvc.async.request-timeout", "600000"); // 10 minutes
            SpringApplication app = new SpringApplication(LapisMain.class);
            app.setDefaultProperties(Collections.singletonMap("server.port", "2345"));
            app.run(argsForSpring);
            return;
        }
        if ("--update-data".equals(args[0])) {
            if (args.length < 2) {
                throw new RuntimeException("Please provide the update steps. - TODO: write help page");
            }
            String[] updateSteps = args[1].split(",");
            ComboPooledDataSource dbPool = DatabaseService.createDatabaseConnectionPool(config.getVineyard());
            Set<String> availableSteps = new HashSet<>() {{
                add(UpdateSteps.loadNG);
                add(UpdateSteps.loadGisaid);
                add(UpdateSteps.loadS3C);
                add(UpdateSteps.loadPangolinAssignment);
                add(UpdateSteps.transformNG);
                add(UpdateSteps.transformGisaid);
                add(UpdateSteps.mergeFromS3C);
                add(UpdateSteps.mergeFromPangolinAssignment);
                add(UpdateSteps.finalTransforms);
                add(UpdateSteps.switchInStaging);
            }};
            for (String updateStep : updateSteps) {
                if (!availableSteps.contains(updateStep)) {
                    throw new RuntimeException("Unknown update step. - TODO: write help page");
                }
            }
            for (String updateStep : updateSteps) {
                switch (updateStep) {
                    case UpdateSteps.loadNG -> new NextstrainGenbankService(
                        dbPool, config.getWorkdir(), config.getMaxNumberWorkers(), config.getNextalignPath()
                    ).updateData();
                    case UpdateSteps.loadGisaid -> new GisaidService(
                        dbPool, config.getWorkdir(), config.getMaxNumberWorkers(), config.getNextalignPath(),
                        config.getGisaidApiConfig(), config.getGeoLocationRulesPath()
                    ).updateData();
                    case UpdateSteps.loadS3C -> new S3CVineyardService(dbPool, config.getS3cVineyard()).updateData();
                    case UpdateSteps.loadPangolinAssignment -> new CovLineagesService(dbPool, config.getWorkdir())
                        .pullGisaidPangoLineageAssignments();
                    case UpdateSteps.transformNG -> new TransformService(dbPool, config.getMaxNumberWorkers())
                        .mergeAndTransform(LapisConfig.Source.NG);
                    case UpdateSteps.transformGisaid -> new TransformService(dbPool, config.getMaxNumberWorkers())
                        .mergeAndTransform(LapisConfig.Source.GISAID);
                    case UpdateSteps.mergeFromS3C -> new TransformService(dbPool, config.getMaxNumberWorkers())
                        .mergeAdditionalMetadataFromS3c();
                    case UpdateSteps.mergeFromPangolinAssignment ->
                        new TransformService(dbPool, config.getMaxNumberWorkers()).mergeFromPangolinAssignment();
                    case UpdateSteps.finalTransforms ->
                        new TransformService(dbPool, config.getMaxNumberWorkers()).finalTransforms();
                    case UpdateSteps.switchInStaging -> new TransformService(dbPool, config.getMaxNumberWorkers())
                        .switchInStagingTables();
                }
            }
        }
    }

}
