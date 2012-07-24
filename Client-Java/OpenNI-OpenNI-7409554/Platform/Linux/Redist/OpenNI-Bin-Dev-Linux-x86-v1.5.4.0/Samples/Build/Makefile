include Common/CommonDefs.mak

.PHONY: all

NETPROJ = 
ifneq "$(realpath /usr/bin/gmcs)" ""
	NETPROJ += SimpleRead.net
	NETPROJ += SimpleViewer.net
	NETPROJ += UserTracker.net
endif

all: $(NETPROJ) NiSampleModule NiSkeletonBenchmark NiCRead NiHandTracker SimpleViewer.java NiUserTracker NiViewer NiSimpleCreate UserTracker.java SimpleRead.java NiBackRecorder NiConvertXToONI NiRecordSynthetic NiSimpleSkeleton NiSimpleRead NiSimpleViewer NiUserSelection NiAudioSample 


.PHONY: NiSampleModule
NiSampleModule:
	$(MAKE) -C ../NiSampleModule

.PHONY: SimpleRead.net
SimpleRead.net:
	$(MAKE) -C ../SimpleRead.net

.PHONY: NiSkeletonBenchmark
NiSkeletonBenchmark:
	$(MAKE) -C ../NiSkeletonBenchmark

.PHONY: NiCRead
NiCRead:
	$(MAKE) -C ../NiCRead

.PHONY: NiHandTracker
NiHandTracker:
	$(MAKE) -C ../NiHandTracker

.PHONY: SimpleViewer.java
SimpleViewer.java:
	$(MAKE) -C ../SimpleViewer.java

.PHONY: NiUserTracker
NiUserTracker:
	$(MAKE) -C ../NiUserTracker

.PHONY: SimpleViewer.net
SimpleViewer.net:
	$(MAKE) -C ../SimpleViewer.net

.PHONY: NiViewer
NiViewer:
	$(MAKE) -C ../NiViewer

.PHONY: NiSimpleCreate
NiSimpleCreate:
	$(MAKE) -C ../NiSimpleCreate

.PHONY: UserTracker.java
UserTracker.java:
	$(MAKE) -C ../UserTracker.java

.PHONY: UserTracker.net
UserTracker.net:
	$(MAKE) -C ../UserTracker.net

.PHONY: SimpleRead.java
SimpleRead.java:
	$(MAKE) -C ../SimpleRead.java

.PHONY: NiBackRecorder
NiBackRecorder:
	$(MAKE) -C ../NiBackRecorder

.PHONY: NiConvertXToONI
NiConvertXToONI:
	$(MAKE) -C ../NiConvertXToONI

.PHONY: NiRecordSynthetic
NiRecordSynthetic:
	$(MAKE) -C ../NiRecordSynthetic

.PHONY: NiSimpleSkeleton
NiSimpleSkeleton:
	$(MAKE) -C ../NiSimpleSkeleton

.PHONY: NiSimpleRead
NiSimpleRead:
	$(MAKE) -C ../NiSimpleRead

.PHONY: NiSimpleViewer
NiSimpleViewer:
	$(MAKE) -C ../NiSimpleViewer

.PHONY: NiUserSelection
NiUserSelection:
	$(MAKE) -C ../NiUserSelection

.PHONY: NiAudioSample
NiAudioSample:
	$(MAKE) -C ../NiAudioSample
