Bean Creation Guideline
=======================

1. Creation Timestamp naming convention - createdOn
```
	@Column(name = "created_on", insertable = false, updatable = false)
	private Date createdOn;
```

2. Last Updated Timestamp naming convention - lastUpdated
