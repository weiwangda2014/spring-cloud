package org.cloud.service.component;

import org.cloud.jpa.domain.component.Activity;
import org.cloud.jpa.repository.component.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ActivityService extends ComponentBaseService<Activity> {

    @Autowired
    private ActivityRepository activityRepository;

    private Long highestId = 0L;

    @PostConstruct
    private void initialize() {
        super.initialize(activityRepository);
        for (Activity activity : cachedComponents) {
            if (activity.getId() > highestId)
                highestId = activity.getId();
        }
    }

    public Activity createActivity() {
        return super.createComponent(new Activity(), "ACTIVITY_" + ++highestId);
    }

    public Activity saveActivity(Activity activity) {
        return super.saveComponent(activity);
    }
}
