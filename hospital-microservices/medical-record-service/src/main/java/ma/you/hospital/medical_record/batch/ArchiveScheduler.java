package ma.you.hospital.medical_record.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/*import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;*/
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ArchiveScheduler {

    private final JobOperator jobOperator;

    // Run archive job every day at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void runArchiveJob() {

            log.info("Starting scheduled archive job");
/*
            String params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters()
                    .toString();

            jobOperator.start("archiveJob", params);

            log.info("Archive job started successfully");
        } catch (Exception e) {
            log.error("Failed to run archive job", e);
        }
    }

    // Alternative: Run every hour
    // @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds

    // Alternative: Run every week on Sunday at 3 AM
    // @Scheduled(cron = "0 0 3 ? * SUN")*/
}}