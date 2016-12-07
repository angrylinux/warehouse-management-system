package com.repository.web.query;

import com.repository.base.BaseController;
import com.repository.entity.CategoryEntity;
import com.repository.entity.ItemEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import static com.repository.Constants.HTML_QUERY_LIST;
import static com.repository.Constants.SESSION_CATEGORIES;
import static com.repository.Constants.SESSION_CATEGORIES_A;
import static com.repository.Constants.SESSION_COMPANIES;
import static com.repository.Constants.TILES_PREFIX;
import static com.repository.Constants.URL_QUERY;
import static com.repository.Constants.URL_QUERY_QUERYITEM;


@Controller
public class QueryController extends BaseController {

    @ModelAttribute
    public void init(HttpSession session) {
        session.setAttribute(SESSION_CATEGORIES, categories());
        session.setAttribute(SESSION_CATEGORIES_A, categoriesA());
        session.setAttribute(SESSION_COMPANIES, companyDao.findAll());
        logger.trace(categories());
        logger.trace(categoriesA());
    }


    /**
     * query
     *
     * @return tiles html view
     */
    @RequestMapping(URL_QUERY)
    public String queryTo() {
        logger.info("query");
        System.out.println(TILES_PREFIX + HTML_QUERY_LIST);
        return TILES_PREFIX + HTML_QUERY_LIST;
    }

    @RequestMapping("/hello")
    @ResponseBody
    public List<ItemEntity> queryTo1(Model model) {
        return new ArrayList<>();
    }

    /**
     * 查询物品
     *
     * @param principal .
     * @param itemCode  .
     * @param itemName  .
     * @param model     .
     * @return list
     */
    @RequestMapping(URL_QUERY_QUERYITEM)
    @ResponseBody
    public List<ItemEntity> queryItem(
            @RequestParam(name = "itemCode", required = false, defaultValue = "") String itemCode,
            @RequestParam(name = "itemName", required = false, defaultValue = "") String itemName,
            @RequestParam(name = "itemCategoryId", required = false, defaultValue = "") String itemCategoryId,
            ModelMap model,
            Principal principal) {
        model.clear();
        List<ItemEntity> result = itemDao.query(new String[]{"itemCode", "itemName"}
                , new String[]{itemCode, itemName});
        result.addAll(itemDao.queryByCategoryId(itemCategoryId));
        model.addAttribute("items", result);
        logger.info("itemCode:" + itemCode);
        logger.info("itemName:" + itemName);
        logger.info("itemCategoryId:" + itemCategoryId);
        logger.info(result.size());
        if (result == null) {
            result = new ArrayList<>();
        }

        if (!itemCode.trim().equals("")) {
            logSerivce.queryItem(principal.getName(), itemCode);
        }
        if (!itemName.trim().equals("")) {
            logSerivce.queryItem(principal.getName(), itemName);
        }
        if (!itemCategoryId.trim().equals("")) {
            logSerivce.queryItem(principal.getName(), itemCategoryId);
        }

        return result;
    }


    public List<CategoryEntity> categories() {
        return categoryDao.findAll();
    }

    private List<CategoryEntity> categoriesA() {
        return categoryDao.findAll().stream().filter(entity -> entity.getCategoryName().endsWith("A")).collect(Collectors.toList());
    }
}
