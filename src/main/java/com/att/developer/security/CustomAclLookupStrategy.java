package com.att.developer.security;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;
import org.springframework.security.util.FieldUtils;
import org.springframework.util.Assert;


/**
 * Stolen from AclBasicLookupStrategy.   Need to override to deal with our primary key being a String instead of a Long.
 * 
 *  Unfortunately, way too much stuff is private to be able to extend of compose of the AclBasicLookupStrategy class.  
 *  
 *  Spring Security 4.0 release that is coming out in 2014 does not appear to address this issue either.
 *  
 *  
 */
public final class CustomAclLookupStrategy implements LookupStrategy {

    public final static String DEFAULT_SELECT_CLAUSE = "select acl_object_identity.object_id_identity, "
        + "acl_entry.ace_order,  "
        + "acl_object_identity.id as acl_id, "
        + "acl_object_identity.parent_object, "
        + "acl_object_identity.entries_inheriting, "
        + "acl_entry.id as ace_id, "
        + "acl_entry.mask,  "
        + "acl_entry.granting,  "
        + "acl_entry.audit_success, "
        + "acl_entry.audit_failure,  "
        + "acl_sid.principal as ace_principal, "
        + "acl_sid.sid as ace_sid,  "
        + "acli_sid.principal as acl_principal, "
        + "acli_sid.sid as acl_sid, "
        + "acl_class.class "
        + "from acl_object_identity "
        + "left join acl_sid acli_sid on acli_sid.id = acl_object_identity.owner_sid "
        + "left join acl_class on acl_class.id = acl_object_identity.object_id_class   "
        + "left join acl_entry on acl_object_identity.id = acl_entry.acl_object_identity "
        + "left join acl_sid on acl_entry.sid = acl_sid.id  "
        + "where ( ";

    private final static String DEFAULT_LOOKUP_KEYS_WHERE_CLAUSE = "(acl_object_identity.id = ?)";

    private final static String DEFAULT_LOOKUP_IDENTITIES_WHERE_CLAUSE = "(acl_object_identity.object_id_identity = ? and acl_class.class = ?)";

    public final static String DEFAULT_ORDER_BY_CLAUSE = ") order by acl_object_identity.object_id_identity"
        + " asc, acl_entry.ace_order asc";

    //~ Instance fields ================================================================================================

    private final AclAuthorizationStrategy aclAuthorizationStrategy;
    private PermissionFactory permissionFactory = new DefaultPermissionFactory();
    private final AclCache aclCache;
    private final PermissionGrantingStrategy grantingStrategy;
    private final JdbcTemplate jdbcTemplate;
    private int batchSize = 50;

    private final Field fieldAces = FieldUtils.getField(AclImpl.class, "aces");
    private final Field fieldAcl = FieldUtils.getField(AccessControlEntryImpl.class, "acl");

    // SQL Customization fields
    private String selectClause = DEFAULT_SELECT_CLAUSE;
    private String lookupPrimaryKeysWhereClause = DEFAULT_LOOKUP_KEYS_WHERE_CLAUSE;
    private String lookupObjectIdentitiesWhereClause = DEFAULT_LOOKUP_IDENTITIES_WHERE_CLAUSE;
    private String orderByClause = DEFAULT_ORDER_BY_CLAUSE;

    //~ Constructors ===================================================================================================

    /**
     * Constructor accepting mandatory arguments
     *
     * @param dataSource to access the database
     * @param aclCache the cache where fully-loaded elements can be stored
     * @param aclAuthorizationStrategy authorization strategy (required)
     *
     * @deprecated Use the version which takes a  {@code PermissionGrantingStrategy} argument instead.
     */
    @Deprecated
    public CustomAclLookupStrategy(DataSource dataSource, AclCache aclCache, AclAuthorizationStrategy aclAuthorizationStrategy, AuditLogger auditLogger) {
        this(dataSource, aclCache, aclAuthorizationStrategy, new DefaultPermissionGrantingStrategy(auditLogger));
    }

    public CustomAclLookupStrategy(DataSource dataSource, AclCache aclCache, AclAuthorizationStrategy aclAuthorizationStrategy, PermissionGrantingStrategy grantingStrategy) {
        Assert.notNull(dataSource, "DataSource required");
        Assert.notNull(aclCache, "AclCache required");
        Assert.notNull(aclAuthorizationStrategy, "AclAuthorizationStrategy required");
        Assert.notNull(grantingStrategy, "grantingStrategy required");
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.aclCache = aclCache;
        this.aclAuthorizationStrategy = aclAuthorizationStrategy;
        this.grantingStrategy = grantingStrategy;
        fieldAces.setAccessible(true);
        fieldAcl.setAccessible(true);

    }

    //~ Methods ========================================================================================================

    private String computeRepeatingSql(String repeatingSql, int requiredRepetitions) {
        assert requiredRepetitions > 0 : "requiredRepetitions must be > 0";

        final String startSql = selectClause;

        final String endSql = orderByClause;

        StringBuilder sqlStringBldr =
            new StringBuilder(startSql.length() + endSql.length() + requiredRepetitions * (repeatingSql.length() + 4));
        sqlStringBldr.append(startSql);

        for (int i = 1; i <= requiredRepetitions; i++) {
            sqlStringBldr.append(repeatingSql);

            if (i != requiredRepetitions) {
                sqlStringBldr.append(" or ");
            }
        }

        sqlStringBldr.append(endSql);

        return sqlStringBldr.toString();
    }

    @SuppressWarnings("unchecked")
    private List<AccessControlEntryImpl> readAces(AclImpl acl) {
        try {
            return (List<AccessControlEntryImpl>) fieldAces.get(acl);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not obtain AclImpl.aces field", e);
        }
    }

    private void setAclOnAce(AccessControlEntryImpl ace, AclImpl acl) {
        try {
            fieldAcl.set(ace, acl);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not or set AclImpl on AccessControlEntryImpl fields", e);
        }
    }

    private void setAces(AclImpl acl, List<AccessControlEntryImpl> aces) {
        try {
            fieldAces.set(acl, aces);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not set AclImpl entries", e);
        }
    }

    /**
     * Locates the primary key IDs specified in "findNow", adding AclImpl instances with StubAclParents to the
     * "acls" Map.
     *
     * @param acls the AclImpls (with StubAclParents)
     * @param findNow Long-based primary keys to retrieve
     * @param sids
     */
    private void lookupPrimaryKeys(final Map<Serializable, Acl> acls, final Set<Long> findNow, final List<Sid> sids) {
        Assert.notNull(acls, "ACLs are required");
        Assert.notEmpty(findNow, "Items to find now required");

        String sql = computeRepeatingSql(lookupPrimaryKeysWhereClause, findNow.size());

        Set<Long> parentsToLookup = jdbcTemplate.query(sql,
            new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    int i = 0;

                    for (Long toFind : findNow) {
                        i++;
                        ps.setLong(i, toFind);
                    }
                }
            }, new ProcessResultSet(acls, sids));

        // Lookup the parents, now that our JdbcTemplate has released the database connection (SEC-547)
        if (parentsToLookup.size() > 0) {
            lookupPrimaryKeys(acls, parentsToLookup, sids);
        }
    }

    /**
     * The main method.
     * <p>
     * WARNING: This implementation completely disregards the "sids" argument! Every item in the cache is expected to
     * contain all SIDs. If you have serious performance needs (e.g. a very large number of
     * SIDs per object identity), you'll probably want to develop a custom {@link LookupStrategy} implementation
     * instead.
     * <p>
     * The implementation works in batch sizes specified by {@link #batchSize}.
     *
     * @param objects the identities to lookup (required)
     * @param sids the SIDs for which identities are required (ignored by this implementation)
     *
     * @return a <tt>Map</tt> where keys represent the {@link ObjectIdentity} of the located {@link Acl} and values
     *         are the located {@link Acl} (never <tt>null</tt> although some entries may be missing; this method
     *         should not throw {@link NotFoundException}, as a chain of {@link LookupStrategy}s may be used
     *         to automatically create entries if required)
     */
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) {
        Assert.isTrue(batchSize >= 1, "BatchSize must be >= 1");
        Assert.notEmpty(objects, "Objects to lookup required");

        // Map<ObjectIdentity,Acl>
        Map<ObjectIdentity, Acl> result = new HashMap<ObjectIdentity, Acl>(); // contains FULLY loaded Acl objects

        Set<ObjectIdentity> currentBatchToLoad = new HashSet<ObjectIdentity>();

        for (int i = 0; i < objects.size(); i++) {
            final ObjectIdentity oid = objects.get(i);
            boolean aclFound = false;

            // Check we don't already have this ACL in the results
            if (result.containsKey(oid)) {
                aclFound = true;
            }

            // Check cache for the present ACL entry
            if (!aclFound) {
                Acl acl = aclCache.getFromCache(oid);

                // Ensure any cached element supports all the requested SIDs
                // (they should always, as our base impl doesn't filter on SID)
                if (acl != null) {
                    if (acl.isSidLoaded(sids)) {
                        result.put(acl.getObjectIdentity(), acl);
                        aclFound = true;
                    } else {
                        throw new IllegalStateException(
                            "Error: SID-filtered element detected when implementation does not perform SID filtering "
                                    + "- have you added something to the cache manually?");
                    }
                }
            }

            // Load the ACL from the database
            if (!aclFound) {
                currentBatchToLoad.add(oid);
            }

            // Is it time to load from JDBC the currentBatchToLoad?
            if ((currentBatchToLoad.size() == this.batchSize) || ((i + 1) == objects.size())) {
                if (currentBatchToLoad.size() > 0) {
                    Map<ObjectIdentity, Acl> loadedBatch = lookupObjectIdentities(currentBatchToLoad, sids);

                    // Add loaded batch (all elements 100% initialized) to results
                    result.putAll(loadedBatch);

                    // Add the loaded batch to the cache

                    for (Acl loadedAcl : loadedBatch.values()) {
                        aclCache.putInCache((AclImpl) loadedAcl);
                    }

                    currentBatchToLoad.clear();
                }
            }
        }

        return result;
    }

    /**
     * Looks up a batch of <code>ObjectIdentity</code>s directly from the database.
     * <p>
     * The caller is responsible for optimization issues, such as selecting the identities to lookup, ensuring the
     * cache doesn't contain them already, and adding the returned elements to the cache etc.
     * <p>
     * This subclass is required to return fully valid <code>Acl</code>s, including properly-configured
     * parent ACLs.
     *
     */
    private Map<ObjectIdentity, Acl> lookupObjectIdentities(final Collection<ObjectIdentity> objectIdentities, List<Sid> sids) {
        Assert.notEmpty(objectIdentities, "Must provide identities to lookup");

        final Map<Serializable, Acl> acls = new HashMap<Serializable, Acl>(); // contains Acls with StubAclParents

        // Make the "acls" map contain all requested objectIdentities
        // (including markers to each parent in the hierarchy)
        String sql = computeRepeatingSql(lookupObjectIdentitiesWhereClause, objectIdentities.size());

        Set<Long> parentsToLookup = jdbcTemplate.query(sql,
            new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                    int i = 0;
                    for (ObjectIdentity oid : objectIdentities) {
                        // Determine prepared statement values for this iteration
                        String type = oid.getType();

                        // No need to check for nulls, as guaranteed non-null by ObjectIdentity.getIdentifier() interface contract
                        Serializable identifier = oid.getIdentifier();
//                        String identifier = oid.getIdentifier().toString();
//                        long id = (Long.valueOf(identifier)).longValue();

                        // Inject values
//                        ps.setLong((2 * i) + 1, id);
//                        ps.setString((2 * i) + 2, type);
                        ps.setObject((2 * i) + 1, identifier);
                        ps.setString((2 * i) + 2, type);
                        i++;
                    }
                }
            }, new ProcessResultSet(acls, sids));

        // Lookup the parents, now that our JdbcTemplate has released the database connection (SEC-547)
        if (parentsToLookup.size() > 0) {
            lookupPrimaryKeys(acls, parentsToLookup, sids);
        }

        // Finally, convert our "acls" containing StubAclParents into true Acls
        Map<ObjectIdentity, Acl> resultMap = new HashMap<ObjectIdentity, Acl>();

        for (Acl inputAcl : acls.values()) {
            Assert.isInstanceOf(AclImpl.class, inputAcl, "Map should have contained an AclImpl");
            Assert.isInstanceOf(Long.class, ((AclImpl) inputAcl).getId(), "Acl.getId() must be Long");

            Acl result = convert(acls, (Long) ((AclImpl) inputAcl).getId());
            resultMap.put(result.getObjectIdentity(), result);
        }

        return resultMap;
    }

    /**
     * The final phase of converting the <code>Map</code> of <code>AclImpl</code> instances which contain
     * <code>StubAclParent</code>s into proper, valid <code>AclImpl</code>s with correct ACL parents.
     *
     * @param inputMap the unconverted <code>AclImpl</code>s
     * @param currentIdentity the current<code>Acl</code> that we wish to convert (this may be
     *
     */
    private AclImpl convert(Map<Serializable, Acl> inputMap, Long currentIdentity) {
        Assert.notEmpty(inputMap, "InputMap required");
        Assert.notNull(currentIdentity, "CurrentIdentity required");

        // Retrieve this Acl from the InputMap
        Acl uncastAcl = inputMap.get(currentIdentity);
        Assert.isInstanceOf(AclImpl.class, uncastAcl, "The inputMap contained a non-AclImpl");

        AclImpl inputAcl = (AclImpl) uncastAcl;

        Acl parent = inputAcl.getParentAcl();

        if ((parent != null) && parent instanceof StubAclParent) {
            // Lookup the parent
            StubAclParent stubAclParent = (StubAclParent) parent;
            parent = convert(inputMap, stubAclParent.getId());
        }

        // Now we have the parent (if there is one), create the true AclImpl
        AclImpl result = new AclImpl(inputAcl.getObjectIdentity(), (Long) inputAcl.getId(), aclAuthorizationStrategy,
                grantingStrategy, parent, null, inputAcl.isEntriesInheriting(), inputAcl.getOwner());

        // Copy the "aces" from the input to the destination

        // Obtain the "aces" from the input ACL
        List<AccessControlEntryImpl> aces = readAces(inputAcl);

        // Create a list in which to store the "aces" for the "result" AclImpl instance
        List<AccessControlEntryImpl> acesNew = new ArrayList<AccessControlEntryImpl>();

        // Iterate over the "aces" input and replace each nested AccessControlEntryImpl.getAcl() with the new "result" AclImpl instance
        // This ensures StubAclParent instances are removed, as per SEC-951
        for (AccessControlEntryImpl ace : aces) {
            setAclOnAce(ace, result);
            acesNew.add(ace);
        }

        // Finally, now that the "aces" have been converted to have the "result" AclImpl instance, modify the "result" AclImpl instance
        setAces(result, acesNew);

        return result;
    }

    /**
     * Sets the {@code PermissionFactory} instance which will be used to convert loaded permission
     * data values to {@code Permission}s. A {@code DefaultPermissionFactory} will be used by default.
     *
     * @param permissionFactory
     */
    public void setPermissionFactory(PermissionFactory permissionFactory) {
        this.permissionFactory = permissionFactory;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * The SQL for the select clause. If customizing in order to modify
     * column names, schema etc, the other SQL customization fields must also be set to match.
     *
     * @param selectClause the select clause, which defaults to {@link #DEFAULT_SELECT_CLAUSE}.
     */
    public void setSelectClause(String selectClause) {
        this.selectClause = selectClause;
    }

    /**
     * The SQL for the where clause used in the <tt>lookupPrimaryKey</tt> method.
     */
    public void setLookupPrimaryKeysWhereClause(String lookupPrimaryKeysWhereClause) {
        this.lookupPrimaryKeysWhereClause = lookupPrimaryKeysWhereClause;
    }

    /**
     * The SQL for the where clause used in the <tt>lookupObjectIdentities</tt> method.
     */
    public void setLookupObjectIdentitiesWhereClause(String lookupObjectIdentitiesWhereClause) {
        this.lookupObjectIdentitiesWhereClause = lookupObjectIdentitiesWhereClause;
    }

    /**
     * The SQL for the "order by" clause used in both queries.
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    //~ Inner Classes ==================================================================================================

    private class ProcessResultSet implements ResultSetExtractor<Set<Long>> {
        private final Map<Serializable, Acl> acls;
        private final List<Sid> sids;

        public ProcessResultSet(Map<Serializable, Acl> acls, List<Sid> sids) {
            Assert.notNull(acls, "ACLs cannot be null");
            this.acls = acls;
            this.sids = sids; // can be null
        }

        /**
         * Implementation of {@link ResultSetExtractor#extractData(ResultSet)}.
         * Creates an {@link Acl} for each row in the {@link ResultSet} and
         * ensures it is in member field <tt>acls</tt>.  Any {@link Acl} with
         * a parent will have the parents id returned in a set.  The returned
         * set of ids may requires further processing.
         * @param rs The {@link ResultSet} to be processed
         * @return a list of parent IDs remaining to be looked up (may be empty, but never <tt>null</tt>)
         * @throws SQLException
         */
        public Set<Long> extractData(ResultSet rs) throws SQLException {
            Set<Long> parentIdsToLookup = new HashSet<Long>(); // Set of parent_id Longs

            while (rs.next()) {
                // Convert current row into an Acl (albeit with a StubAclParent)
                convertCurrentResultIntoObject(acls, rs);

                // Figure out if this row means we need to lookup another parent
                long parentId = rs.getLong("parent_object");

                if (parentId != 0) {
                    // See if it's already in the "acls"
                    if (acls.containsKey(new Long(parentId))) {
                        continue; // skip this while iteration
                    }

                    // Now try to find it in the cache
                    MutableAcl cached = aclCache.getFromCache(new Long(parentId));

                    if ((cached == null) || !cached.isSidLoaded(sids)) {
                        parentIdsToLookup.add(new Long(parentId));
                    } else {
                        // Pop into the acls map, so our convert method doesn't
                        // need to deal with an unsynchronized AclCache
                        acls.put(cached.getId(), cached);
                    }
                }
            }

            // Return the parents left to lookup to the caller
            return parentIdsToLookup;
        }

        /**
         * Accepts the current <code>ResultSet</code> row, and converts it into an <code>AclImpl</code> that
         * contains a <code>StubAclParent</code>
         *
         * @param acls the Map we should add the converted Acl to
         * @param rs the ResultSet focused on a current row
         *
         * @throws SQLException if something goes wrong converting values
         */
        private void convertCurrentResultIntoObject(Map<Serializable, Acl> acls, ResultSet rs) throws SQLException {
            Long id = new Long(rs.getLong("acl_id"));

            // If we already have an ACL for this ID, just create the ACE
            Acl acl = acls.get(id);

            if (acl == null) {
                // Make an AclImpl and pop it into the Map
//                ObjectIdentity objectIdentity = new ObjectIdentityImpl(rs.getString("class"),
//                        Long.valueOf(rs.getLong("object_id_identity")));
            	
            	ObjectIdentity objectIdentity = new ObjectIdentityImpl(rs.getString("class"), 
            			(Serializable)rs.getObject("object_id_identity"));
            	

                Acl parentAcl = null;
                long parentAclId = rs.getLong("parent_object");

                if (parentAclId != 0) {
                    parentAcl = new StubAclParent(Long.valueOf(parentAclId));
                }

                boolean entriesInheriting = rs.getBoolean("entries_inheriting");
                Sid owner;

                if (rs.getBoolean("acl_principal")) {
                    owner = new PrincipalSid(rs.getString("acl_sid"));
                } else {
                    owner = new GrantedAuthoritySid(rs.getString("acl_sid"));
                }

                acl = new AclImpl(objectIdentity, id, aclAuthorizationStrategy, grantingStrategy, parentAcl, null,
                        entriesInheriting, owner);

                acls.put(id, acl);
            }

            // Add an extra ACE to the ACL (ORDER BY maintains the ACE list order)
            // It is permissible to have no ACEs in an ACL (which is detected by a null ACE_SID)
            if (rs.getString("ace_sid") != null) {
                Long aceId = new Long(rs.getLong("ace_id"));
                Sid recipient;

                if (rs.getBoolean("ace_principal")) {
                    recipient = new PrincipalSid(rs.getString("ace_sid"));
                } else {
                    recipient = new GrantedAuthoritySid(rs.getString("ace_sid"));
                }

                int mask = rs.getInt("mask");
                Permission permission = permissionFactory.buildFromMask(mask);
                boolean granting = rs.getBoolean("granting");
                boolean auditSuccess = rs.getBoolean("audit_success");
                boolean auditFailure = rs.getBoolean("audit_failure");

                AccessControlEntryImpl ace = new AccessControlEntryImpl(aceId, acl, recipient, permission, granting,
                        auditSuccess, auditFailure);

                //Field acesField = FieldUtils.getField(AclImpl.class, "aces");
                List<AccessControlEntryImpl> aces = readAces((AclImpl)acl);

                // Add the ACE if it doesn't already exist in the ACL.aces field
                if (!aces.contains(ace)) {
                    aces.add(ace);
                }
            }
        }
    }

    private class StubAclParent implements Acl {
        private final Long id;

        public StubAclParent(Long id) {
            this.id = id;
        }

        public List<AccessControlEntry> getEntries() {
            throw new UnsupportedOperationException("Stub only");
        }

        public Long getId() {
            return id;
        }

        public ObjectIdentity getObjectIdentity() {
            throw new UnsupportedOperationException("Stub only");
        }

        public Sid getOwner() {
            throw new UnsupportedOperationException("Stub only");
        }

        public Acl getParentAcl() {
            throw new UnsupportedOperationException("Stub only");
        }

        public boolean isEntriesInheriting() {
            throw new UnsupportedOperationException("Stub only");
        }

        public boolean isGranted(List<Permission> permission, List<Sid> sids, boolean administrativeMode)
            throws NotFoundException, UnloadedSidException {
            throw new UnsupportedOperationException("Stub only");
        }

        public boolean isSidLoaded(List<Sid> sids) {
            throw new UnsupportedOperationException("Stub only");
        }
    }
}

