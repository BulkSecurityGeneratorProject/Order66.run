package run.order66.application.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import run.order66.application.service.SchedulingService;
import run.order66.application.service.SchedulingTaskService;
import run.order66.application.domain.Rule;
import run.order66.application.domain.Scheduling;
import run.order66.application.domain.enumeration.TriggerEnum;
import run.order66.application.exception.UnsuportedTriggerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.order66.application.service.ExecutorTaskFactoryService;

import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;

@Service
@Transactional
public class SchedulingTaskServiceImpl implements SchedulingTaskService {

    private final Logger log = LoggerFactory.getLogger(SchedulingTaskServiceImpl.class);

    @Autowired
    private SchedulingService schedulingService;
    
    @Autowired
    private TaskScheduler scheduler;
    
    @Autowired
    private ExecutorTaskFactoryService executorTaskFactory;
    
    private Map<Scheduling, List<ScheduledFuture<?>>> schedulingMap;
    private Map<Rule, List<ScheduledFuture<?>>> ruleMap;
    
    public SchedulingTaskServiceImpl() {
    	super();
    	this.schedulingMap = new HashMap<Scheduling, List<ScheduledFuture<?>>>();
    	this.ruleMap = new HashMap<Rule, List<ScheduledFuture<?>>>();
    	
    }
    
    
    public void createTrigger(Rule rule, Scheduling scheduling) throws UnsuportedTriggerException {
    	switch(scheduling.getTrigger()) {
    		case cronSchedule:
    			ScheduledFuture<?> schedule = createCronTrigger(rule, scheduling.getQuartzCronRule());
    			if(schedule != null) {
    				this.registerInMermoy(scheduling, rule, schedule);
    			}
    			break;
    		default:
				throw new UnsuportedTriggerException();
    	}
    }

	private void registerInMermoy(Scheduling scheduling, Rule rule, ScheduledFuture<?> schedule) {
		// TODO Auto-generated method stub
		List schedules;
		if(this.schedulingMap.containsKey(scheduling)) {
			schedules = this.schedulingMap.get(scheduling);
		} else {
			schedules = new ArrayList<ScheduledFuture<?>>();
		}
		schedules.add(schedule);
		this.schedulingMap.put(scheduling, schedules);
			
		if(this.ruleMap.containsKey(scheduling)) {
			schedules = this.ruleMap.get(rule);
		} else {
			schedules = new ArrayList<ScheduledFuture<?>>();
		}
		schedules.add(schedule);
		this.ruleMap.put(rule, schedules);
	}


	public ScheduledFuture<?> createCronTrigger(Rule rule, String cronRule) {
		try {
			Trigger trigger = new CronTrigger(cronRule);
			Runnable task = executorTaskFactory.createTask(rule);
			ScheduledFuture<?> schedule = scheduler.schedule(task, trigger);
			return schedule;
		} catch(IllegalArgumentException e) {
			log.error("Cron schedule rule {} is invalid cancel scheduling rule {}, \n {}", cronRule, rule.getRuleName(), e.getMessage());
			return null;
		}
	}
	@Override
	public void unregisterJobFromRule(Rule rule) {
		log.info("Unregister all job for rule : " + rule.getId());
		if(this.ruleMap.containsKey(rule)) {
			List<ScheduledFuture<?>> jobs = this.ruleMap.get(rule);
			for(ScheduledFuture<?> job: jobs) {
				if(job != null)
					job.cancel(true);
			}
		}
	}

	@Override
	public void unregisterJobFromScheduling(Scheduling scheduling) {
		log.info("Unregister all job for scheduling : " + scheduling.getId());
		if(this.schedulingMap.containsKey(scheduling)) {
			List<ScheduledFuture<?>> jobs = this.schedulingMap.get(scheduling);
			for(ScheduledFuture<?> job: jobs) {
				if(job != null)
					job.cancel(true);
			}
		}
	}
}
