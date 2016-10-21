SUMMARY = "Enhances systemd compatilibity with existing SysVinit scripts"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r29"

DEPENDS = "systemd-systemctl-native"

S = "${WORKDIR}"

inherit distro_features_check

ALLOW_EMPTY_${PN} = "1"

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_DISABLED_SYSV_SERVICES = " \
  busybox-udhcpc \
  hwclock \
  networking \
  nfsserver \
  nfscommon \
  syslog.busybox \
"

pkg_postinst_${PN} () {

	cd $D${sysconfdir}/init.d  ||  exit 0

	echo "Disabling the following sysv scripts: "

	if [ -n "$D" ]; then
		OPTS="--root=$D"
	else
		OPTS=""
	fi

	for i in ${SYSTEMD_DISABLED_SYSV_SERVICES} ; do
		if [ -e $i -o -e $i.sh ]  &&   ! [ -e $D${sysconfdir}/systemd/system/$i.service -o -e $D${systemd_unitdir}/system/$i.service ] ; then
			echo -n "$i: "
			systemctl $OPTS mask $i.service
		fi
	done
	echo
}

RDEPENDS_${PN} = "systemd"
