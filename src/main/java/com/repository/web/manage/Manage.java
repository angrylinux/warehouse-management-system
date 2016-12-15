package com.repository.web.manage;

import com.repository.dao.ItemApplicationDao;
import com.repository.dao.ItemApplicationOperationDao;
import com.repository.entity.ItemApplicationEntity;
import com.repository.entity.ItemApplicationOperationEntity;
import com.repository.model.SimpleRes;
import com.repository.service.OutStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.sql.Date;
import java.util.List;

import static com.repository.common.Constants.*;

/**
 * Created by Finderlo on 2016/12/8.
 */
@Controller
public class Manage {

    @Autowired
    ItemApplicationOperationDao applicationOperationDao;

    @Autowired
    ItemApplicationDao applicationDao;

    /**
     * 管理需要审核的申请网页 返回view
     *
     * @return view
     */
    @RequestMapping(URL_MANAGE_EXAMEINE)
    public String manage(Model model) {
        List datas = applicationOperationDao.query("states", APPLY_NEED_EXAMINE, true);
        System.out.println("申请单数量：" + datas.size());
        model.addAttribute("history", datas);
        return TILES_PREFIX + HTML_MANAGE_EXAMINE;
    }

    /**
     * 获取申请单的详情
     *
     * @retuen simres.conent=ApplyCompound
     */
    @RequestMapping(value = URL_APPLY_INFO_JSON, method = RequestMethod.GET)
    @ResponseBody
    public SimpleRes getApplyInfo(@RequestParam(value = "application_id", required = false) String application_id) {
        if (application_id == null || application_id.trim().equals("")) {
            return SimpleRes.error("ID不能为空");
        }
        return SimpleRes.success(
                new ApplyCompound(
                        applicationOperationDao.findById(application_id),
                        applicationDao.query("applicationId", application_id, false))
        );
    }

    @Autowired
    OutStorageService outStorageService;

    /**
     * 审核申请单，传入参数：states:true、false；apply_id:
     * true 为通过审核申请单，false 为审核不通过
     * apply_id 为审核单的id
     */
    @RequestMapping(value = URL_MANAGE_PASSEXAMINE, method = RequestMethod.POST)
    public SimpleRes passExamine(
            Principal principal,
            @RequestParam("states") boolean states,
            @RequestParam("apply_id") String apply_id) {
        //通过：创建出库单，然后修改申请单状态，修改物品表，修改入库管理表，发送成功消息（给用户），记录日志
        if (states) {
            try {
                outStorageService.outStorage(principal, apply_id);
            } catch (Exception e) {
                return SimpleRes.error();
            }
        }

        return SimpleRes.success();
    }

    private static class ApplyCompound {
        private String applicationId;
        private String usersId;
        private String examineId;
        private String states;
        private Date statesTime;
        private Date applicationTime;
        private List<ItemApplicationEntity> items;

        public ApplyCompound(ItemApplicationOperationEntity applicationOperation, List<ItemApplicationEntity> itemApplicationEntities) {
            this.applicationId = applicationOperation.getApplicationId();
            this.usersId = applicationOperation.getUsersId();
            this.examineId = applicationOperation.getExamineId();
            this.states = applicationOperation.getStates();
            this.statesTime = applicationOperation.getStatesTime();
            this.applicationTime = applicationOperation.getApplicationTime();
            this.items = itemApplicationEntities;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getUsersId() {
            return usersId;
        }

        public void setUsersId(String usersId) {
            this.usersId = usersId;
        }

        public String getExamineId() {
            return examineId;
        }

        public void setExamineId(String examineId) {
            this.examineId = examineId;
        }

        public String getStates() {
            return states;
        }

        public void setStates(String states) {
            this.states = states;
        }

        public Date getStatesTime() {
            return statesTime;
        }

        public void setStatesTime(Date statesTime) {
            this.statesTime = statesTime;
        }

        public Date getApplicationTime() {
            return applicationTime;
        }

        public void setApplicationTime(Date applicationTime) {
            this.applicationTime = applicationTime;
        }

        public List<ItemApplicationEntity> getItems() {
            return items;
        }

        public void setItems(List<ItemApplicationEntity> items) {
            this.items = items;
        }
    }

}

