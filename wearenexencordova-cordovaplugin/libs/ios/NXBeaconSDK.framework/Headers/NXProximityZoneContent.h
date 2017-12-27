
#import <Foundation/Foundation.h>
#import "NXProximityZoneType.h"
#import <CoreLocation/CoreLocation.h>

@interface NXProximityZoneContent : NSObject
@property(nonatomic,copy) NSString * identifier;
@property(nonatomic,copy) NSString * name;
@property(nonatomic,copy) NSString * message;
@property(nonatomic,copy) NSString * language;
@property(nonatomic,readonly) NXProximityZoneType type;
@property(nonatomic,copy) NSString * title;
@property(nonatomic,copy) NSString * titleImageUrl;
@property(nonatomic,copy) NSString * categoryIdentifier;
@property(nonatomic,assign) NSTimeInterval repeatDelay;
@property(nonatomic,assign) NSTimeInterval dwellingTime;
@property(nonatomic,assign) CLProximity proximity;
@property(nonatomic,assign) NSInteger notifyAfterEventsCount;
@property(nonatomic,assign) NSTimeInterval notifyAfterEventsInterval;
@property(nonatomic,assign) BOOL eventIsEnter;
@property(nonatomic,copy) NSDate * firstSeenOn;
@property(nonatomic, copy) NSDate * blockedUntilDate;
@property(nonatomic, copy) NSString *messageIconUrl;
@property (nonatomic, copy) NSString *richMediaUrl;


@property(nonatomic, readwrite) BOOL isUpdatedLocalProperties;

// these properties are linked to 'notifyAfterEventsCount'
// 'eventDetectionCounter' counts the number of events
// 'firstEventDetectedOn' = the date when the counter was set to 1
// if firstEventDetectedOn was longer than 'notifyAfterEventsInterval' ago, we restart counting
// and change firstEventDetectedOn
@property(nonatomic,copy,readonly) NSDate * firstEventDetectedOn;
@property(nonatomic,readonly) NSInteger eventDetectionCounter;

// does NOT take dwelling time into account!
-(BOOL)canTriggerAction:(CLProximity)zoneProximity;
-(BOOL)canTriggerAction;

// called whenever the conditions are met to increase the counter
-(void)updateEventDetectionCounter;

-(void)trigger;

-(void)updateFromContent:(NXProximityZoneContent *)updatedContent;

@end
