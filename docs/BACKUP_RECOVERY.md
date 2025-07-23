# Backup and Recovery Procedures

## Overview

This document outlines the backup and recovery procedures for the Spring Boot Drools Integration application. These procedures ensure business continuity and data protection in production environments.

## Backup Strategy

### 1. Rule Files Backup

#### Automated Rule Backup
- **Location**: `/tmp/rules/backups` (configurable via `app.rules.backup.dir`)
- **Frequency**: Automatic backup on rule updates
- **Retention**: 30 days (configurable)
- **Format**: Timestamped directories with original file structure

#### Manual Rule Backup
```bash
# Create manual backup of all rule files
mkdir -p /backup/rules/$(date +%Y%m%d_%H%M%S)
cp -r /app/rules/* /backup/rules/$(date +%Y%m%d_%H%M%S)/
```

#### Rule Version Control
- All rule changes are tracked with timestamps
- Decision table versions are maintained
- Rollback capability to previous rule versions

### 2. Configuration Backup

#### Application Configuration
```bash
# Backup application configuration
mkdir -p /backup/config/$(date +%Y%m%d_%H%M%S)
cp /app/config/application*.properties /backup/config/$(date +%Y%m%d_%H%M%S)/
cp /app/config/application*.yml /backup/config/$(date +%Y%m%d_%H%M%S)/
```

#### Environment Variables
```bash
# Backup environment configuration
env | grep -E '^(SPRING_|APP_|DROOLS_)' > /backup/config/$(date +%Y%m%d_%H%M%S)/environment.env
```

### 3. Database Backup

#### H2 Database (Development)
```bash
# Backup H2 database files
cp /app/data/ruledb.* /backup/database/$(date +%Y%m%d_%H%M%S)/
```

#### Production Database
```bash
# PostgreSQL backup example
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME > /backup/database/$(date +%Y%m%d_%H%M%S)/ruledb_backup.sql

# MySQL backup example
mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > /backup/database/$(date +%Y%m%d_%H%M%S)/ruledb_backup.sql
```

### 4. Application State Backup

#### Metrics and Logs
```bash
# Backup application logs
tar -czf /backup/logs/$(date +%Y%m%d_%H%M%S)_logs.tar.gz /app/logs/

# Backup metrics data (if using local storage)
tar -czf /backup/metrics/$(date +%Y%m%d_%H%M%S)_metrics.tar.gz /app/metrics/
```

## Automated Backup Scripts

### Daily Backup Script
```bash
#!/bin/bash
# daily-backup.sh

BACKUP_DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_ROOT="/backup"
APP_ROOT="/app"

# Create backup directories
mkdir -p $BACKUP_ROOT/{rules,config,database,logs}/$BACKUP_DATE

# Backup rules
cp -r $APP_ROOT/rules/* $BACKUP_ROOT/rules/$BACKUP_DATE/

# Backup configuration
cp $APP_ROOT/config/application*.properties $BACKUP_ROOT/config/$BACKUP_DATE/
env | grep -E '^(SPRING_|APP_|DROOLS_)' > $BACKUP_ROOT/config/$BACKUP_DATE/environment.env

# Backup database
if [ "$DB_TYPE" = "postgresql" ]; then
    pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME > $BACKUP_ROOT/database/$BACKUP_DATE/ruledb_backup.sql
elif [ "$DB_TYPE" = "mysql" ]; then
    mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_ROOT/database/$BACKUP_DATE/ruledb_backup.sql
fi

# Backup logs
tar -czf $BACKUP_ROOT/logs/$BACKUP_DATE/logs.tar.gz $APP_ROOT/logs/

# Cleanup old backups (keep 30 days)
find $BACKUP_ROOT -type d -mtime +30 -exec rm -rf {} \;

echo "Backup completed: $BACKUP_DATE"
```

### Weekly Full Backup Script
```bash
#!/bin/bash
# weekly-backup.sh

BACKUP_DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_ROOT="/backup/weekly"
APP_ROOT="/app"

# Create full application backup
mkdir -p $BACKUP_ROOT/$BACKUP_DATE

# Full application directory backup
tar -czf $BACKUP_ROOT/$BACKUP_DATE/full_application.tar.gz $APP_ROOT/

# Database backup
if [ "$DB_TYPE" = "postgresql" ]; then
    pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME > $BACKUP_ROOT/$BACKUP_DATE/ruledb_backup.sql
fi

# Cleanup old weekly backups (keep 12 weeks)
find $BACKUP_ROOT -type d -mtime +84 -exec rm -rf {} \;

echo "Weekly backup completed: $BACKUP_DATE"
```

## Recovery Procedures

### 1. Rule Recovery

#### Restore from Backup
```bash
# List available rule backups
ls -la /backup/rules/

# Restore specific rule backup
RESTORE_DATE="20240723_105500"
cp -r /backup/rules/$RESTORE_DATE/* /app/rules/

# Restart application to reload rules
curl -X POST http://localhost:8080/actuator/shutdown
```

#### Rollback to Previous Version
```bash
# Use rule management API to rollback
curl -X POST http://localhost:8080/api/v1/rules/rollback \
  -H "Content-Type: application/json" \
  -d '{"version": "previous"}'
```

### 2. Configuration Recovery

#### Restore Application Configuration
```bash
# Restore configuration files
RESTORE_DATE="20240723_105500"
cp /backup/config/$RESTORE_DATE/application*.properties /app/config/

# Restore environment variables
source /backup/config/$RESTORE_DATE/environment.env

# Restart application
systemctl restart spring-drools-integration
```

### 3. Database Recovery

#### PostgreSQL Recovery
```bash
# Stop application
systemctl stop spring-drools-integration

# Drop and recreate database
dropdb -h $DB_HOST -U $DB_USER $DB_NAME
createdb -h $DB_HOST -U $DB_USER $DB_NAME

# Restore from backup
RESTORE_DATE="20240723_105500"
psql -h $DB_HOST -U $DB_USER -d $DB_NAME < /backup/database/$RESTORE_DATE/ruledb_backup.sql

# Start application
systemctl start spring-drools-integration
```

#### MySQL Recovery
```bash
# Stop application
systemctl stop spring-drools-integration

# Restore database
RESTORE_DATE="20240723_105500"
mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME < /backup/database/$RESTORE_DATE/ruledb_backup.sql

# Start application
systemctl start spring-drools-integration
```

### 4. Full System Recovery

#### Complete Application Restore
```bash
# Stop application
systemctl stop spring-drools-integration

# Restore from weekly backup
RESTORE_DATE="20240723_105500"
tar -xzf /backup/weekly/$RESTORE_DATE/full_application.tar.gz -C /

# Restore database
psql -h $DB_HOST -U $DB_USER -d $DB_NAME < /backup/weekly/$RESTORE_DATE/ruledb_backup.sql

# Start application
systemctl start spring-drools-integration

# Verify recovery
curl http://localhost:8080/actuator/health
```

## Disaster Recovery

### 1. Recovery Time Objectives (RTO)
- **Critical Systems**: 15 minutes
- **Non-Critical Systems**: 1 hour
- **Full System Recovery**: 4 hours

### 2. Recovery Point Objectives (RPO)
- **Rule Changes**: 0 minutes (immediate backup)
- **Configuration Changes**: 1 hour
- **Database Changes**: 15 minutes

### 3. Disaster Recovery Steps

1. **Assess Damage**
   - Identify failed components
   - Determine data loss extent
   - Prioritize recovery order

2. **Infrastructure Recovery**
   - Provision new infrastructure if needed
   - Restore network connectivity
   - Configure security settings

3. **Application Recovery**
   - Restore application files
   - Restore configuration
   - Restore database

4. **Validation**
   - Run health checks
   - Verify rule execution
   - Test critical functionality

5. **Monitoring**
   - Monitor system performance
   - Check for errors
   - Validate metrics collection

## Backup Monitoring

### Health Checks
```bash
# Check backup directory space
df -h /backup

# Verify recent backups
find /backup -name "*$(date +%Y%m%d)*" -type d

# Test backup integrity
tar -tzf /backup/logs/latest/logs.tar.gz > /dev/null && echo "Backup OK" || echo "Backup FAILED"
```

### Alerting
- Monitor backup job completion
- Alert on backup failures
- Monitor backup storage space
- Validate backup integrity

## Best Practices

### 1. Backup Security
- Encrypt sensitive backups
- Secure backup storage locations
- Implement access controls
- Regular security audits

### 2. Testing
- Regular recovery testing
- Document recovery procedures
- Train operations team
- Validate backup integrity

### 3. Documentation
- Keep procedures up-to-date
- Document all changes
- Maintain recovery runbooks
- Regular procedure reviews

## Compliance and Retention

### Retention Policies
- **Daily Backups**: 30 days
- **Weekly Backups**: 12 weeks
- **Monthly Backups**: 12 months
- **Yearly Backups**: 7 years

### Compliance Requirements
- Follow organizational data retention policies
- Implement secure deletion procedures
- Maintain audit trails
- Regular compliance reviews

## Emergency Contacts

### Operations Team
- **Primary**: ops-team@company.com
- **Secondary**: backup-ops@company.com
- **Emergency**: +1-555-0123

### Vendor Support
- **Database Support**: db-support@vendor.com
- **Infrastructure Support**: infra-support@vendor.com
- **Application Support**: app-support@vendor.com

---

**Last Updated**: 2024-07-23  
**Version**: 1.0  
**Owner**: Operations Team