package com.att.developer.controller;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Organization;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.ApiBundleService;

@Controller
@RequestMapping("/uauth/apiBundle")
public class ApiBundleController {

    private final Logger logger = LogManager.getLogger();

    @Resource
    private ApiBundleService apiBundleService;

    @Resource
    private PermissionManager permissionManager;

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String getEdit(@RequestParam(value = "id", required = true) String id, Model model, @ModelAttribute SessionUser sessionUser) {
        logger.debug("Received request to show edit page");

        
        logger.debug("user from advice is " + sessionUser);
        
        // Retrieve existing post and add to model
        // This is the formBackingOBject
        model.addAttribute("postAttribute", apiBundleService.getApiBundle(id));

        // Add source to model to help us determine the source of the JSP page
        model.addAttribute("source", "Personal");

        // This will resolve to /WEB-INF/jsp/crud-personal/editpage.jsp
        return "jsp/apiBundle/editpage.jsp";
    }
    
    /**
     * Saves the edited post from the Edit page and returns a result page.
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String getEditPage(@ModelAttribute("postAttribute") ApiBundle bundle, @RequestParam(value="id", required=true) String id, 
            @RequestParam(value="comment", required=false) String comment, Model model,  @ModelAttribute SessionUser sessionUser) {
        logger.debug("Received request to view edit page");

        // Re-assign id
        bundle.setId(id);
        // Assign new date

        bundle.setStartDate(Instant.now());
        if (comment != null) {
            bundle.setComments(comment);
        }

        // Delegate to service
        if (apiBundleService.edit(bundle) != null) {
            // Add result to model
            model.addAttribute("result", "Entry has been edited successfully! " + bundle);
        } else {
            // Add result to model
            model.addAttribute("result", "You're not allowed to perform that action! or sql failed");
        }

        // Add source to model to help us determine the source of the JSP page
        model.addAttribute("source", "Edit Bundle");
        
        
        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());
        

        // This will resolve to /WEB-INF/jsp/crud-personal/resultpage.jsp
        return "jsp/apiBundle/resultpage.jsp";
    }

    /**
     * Retrieves the Add page
     * <p>
     * Access-control is placed here (instead in the service) because we don't
     * want to show this page if the client is unauthorized and because the new
     * object doesn't have an id. The hasPermission requires an existing id!
     */
    // TODO: Not working, is the Proxy running? Security context correct?
    // @PreAuthorize("hasRole('ROLE_ADMINISTRAXXXX')")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String getAdd(Model model) {
        logger.debug("Received request to show add page");

        // Create new post and add to model
        // This is the formBackingOBject
        model.addAttribute("postAttribute", new ApiBundle());

        // Add source to model to help us determine the source of the JSP page
        model.addAttribute("source", "Add Bundle Page");

        return "jsp/apiBundle/addpage.jsp";
    }
	
    /**
     * Saves a new post from the Add page and returns a result page.
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String getAddPage(@ModelAttribute("postAttribute") ApiBundle bundle, Model model,  @ModelAttribute SessionUser sessionUser) {
        logger.debug("Received request to view add page");

        User user = sessionUser.getUser();

        String bundleId = UUID.randomUUID().toString();
        bundle.setId(bundleId);
        bundle.setName("b " + bundleId);
        bundle.setComments(bundle.getComments() + " created by " + user.getLogin());
        bundle.setStartDate(Instant.now());
        bundle.setEndDate(Instant.now());

        ApiBundle createdBundle = apiBundleService.create(bundle, user);
        // Delegate to service
        if (createdBundle != null) {
            // Success. Add result to model
            model.addAttribute("result", "Entry has been added successfully! " + bundle);
            // now grant permission to whole organization
            // apiBundleService.grantPermission(createdBundle,
            // user.getDefaultOrganization());
        } else {
            // Failure. Add result to model
            model.addAttribute("result", "You're not allowed to perform that action (maybe) something failed ");
        }

        // Add source to model to help us determine the source of the JSP page
        model.addAttribute("source", "Personal");

        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());

        // This will resolve to /WEB-INF/jsp/crud-personal/resultpage.jsp
        return "jsp/apiBundle/resultpage.jsp";
    }
    
    
    /**
     * Deletes an existing post and returns a result page.
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String getDeletePage(@RequestParam(value = "id", required = true) String id, Model model, @ModelAttribute SessionUser sessionUser) {
        logger.debug("Received request to view delete page");

        // Create new post
        ApiBundle bundle = new ApiBundle(id);

        apiBundleService.delete(bundle);

        // Add source to model to help us determine the source of the JSP page
        model.addAttribute("source", "Delete Bundle Page");

        // Add our current role and username
        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());
        
        // This will resolve to /WEB-INF/jsp/crud-personal/resultpage.jsp
        return "jsp/apiBundle/resultpage.jsp";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model, @ModelAttribute SessionUser sessionUser) {
        
        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());
        

        List<ApiBundle> allApiBundles = apiBundleService.getAll();
        model.addAttribute("allBundles", allApiBundles);

        return "jsp/apiBundle/allbundlespage.jsp";
    }
    
    
    @RequestMapping(value = "/grantPermission", method = RequestMethod.POST)
    public String grantPermission(Model model, @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "orgId", required = true) String orgId, @ModelAttribute SessionUser sessionUser) {

        model.addAttribute("source", "grantPermission");

        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());
        
        
        User actor = sessionUser.getUser();
        Organization org = new Organization();
        org.setId(orgId);
        apiBundleService.grantPermission(new ApiBundle(id), org, actor);

        // User penny = new User();
        // penny.setId("3_penny");
        //
        // User leonard = new User();
        // leonard.setId("2_leonard");
        //
        // permissionManager.grantPermissions(ApiBundle.class, "1Bundle", penny,
        // BasePermission.ADMINISTRATION);
        // permissionManager.grantPermissions(ApiBundle.class, "1Bundle",
        // leonard, BasePermission.READ);

        model.addAttribute("result", "Permission has been granted successfully ! " + id);

        return "jsp/apiBundle/resultpage.jsp";
    }
    
    
    @RequestMapping(value="/removePermission", method=RequestMethod.POST)
    public String removePermission(Model model, @RequestParam(value="id", required=true) String id, 
            @RequestParam(value="orgId", required=true) String orgId, @ModelAttribute SessionUser sessionUser){
    	
       	model.addAttribute("source", "remove Permission");
       	
        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());
    	
    	User actor = sessionUser.getUser();
    	
    	Organization org = new Organization();
    	org.setId(orgId);
    	apiBundleService.removeAllPermissions(new ApiBundle(id), org, actor);
    	
    	
    	model.addAttribute("result", "Permission has been removed successfully ! " + id);
	
    	
    	return "jsp/apiBundle/resultpage.jsp";
    }
  
    
//     @RequestMapping(value="/initialize", method=RequestMethod.GET)
    public String initialize(Model model, @ModelAttribute SessionUser sessionUser) {

        model.addAttribute("source", "Initialize");
        
        // Add our current authorities and username
        model.addAttribute("authoritiesFromSessionUser", sessionUser.getAuthorities());
        model.addAttribute("username", sessionUser.getUsername());

        // Create acl_object_identity rows (and also acl_class rows as needed)
        Set<String> objIdentitifiers = new HashSet<>();
        objIdentitifiers.add("1Bundle");
        objIdentitifiers.add("2Bundle");
        objIdentitifiers.add("3Bundle");
        objIdentitifiers.add("6BundleStringIdentifier");

        for (String identifier : objIdentitifiers) {
            this.permissionManager.createAcl(ApiBundle.class, identifier);
        }

        // Now grant some permissions
        User penny = new User();
        penny.setId("3_penny");

        User leonard = new User();
        leonard.setId("2_leonard");

        permissionManager.grantPermissions(ApiBundle.class, "6BundleStringIdentifier", penny, BasePermission.WRITE);

        permissionManager.grantPermissions(ApiBundle.class, "1Bundle", penny, BasePermission.ADMINISTRATION);
        permissionManager.grantPermissions(ApiBundle.class, "1Bundle", leonard, BasePermission.READ);

        permissionManager.grantPermissions(ApiBundle.class, "2Bundle", penny, new CumulativePermission()
                .set(BasePermission.WRITE).set(BasePermission.READ));
        permissionManager.grantPermissions(ApiBundle.class, "3Bundle", leonard, BasePermission.WRITE);

        // owner block
        permissionManager.changeOwner(ApiBundle.class, "1Bundle", penny);
        permissionManager.changeOwner(ApiBundle.class, "2Bundle", penny);
        permissionManager.changeOwner(ApiBundle.class, "3Bundle", penny);
        permissionManager.changeOwner(ApiBundle.class, "6BundleStringIdentifier", penny);

        return "jsp/apiBundle/resultpage.jsp";
    }
            
}

