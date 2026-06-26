import { Avatar, Skeleton } from "@mantine/core";
import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { getUserProfile } from "../../../Service/UserService";
import useProtectedImage from "../../../Utility/useProtectedImage";
import { getPatient } from "../../../Service/PatientProfileService";
import { bloodGroupMap } from "../../../Data/DropDownData";
import { countAppointmentByPatient, getMedicineConsumeByPatient } from "../../../Service/AppointmentService";

const Welcome = () => {
  const user = useSelector((state: any) => state.user);
  const [picId, setPicId] = useState<string | null>(null);
  const [patientInfo, setPatientInfo] = useState<any>({});
  const [totalVisits, setTotalVisits] = useState<number | null>(null);
  const [medCount, setMedCount] = useState<number | null>(null);

  useEffect(() => {
    if (!user) return;
    getUserProfile(user.id).then((res) => setPicId(res)).catch(console.error);
    getPatient(user.profileId).then((res) => setPatientInfo(res)).catch(console.error);
    countAppointmentByPatient(user.profileId)
      .then((res: any[]) => setTotalVisits(res.reduce((s, m) => s + (m.count ?? 0), 0)))
      .catch(() => setTotalVisits(0));
    getMedicineConsumeByPatient(user.profileId)
      .then((res: any[]) => setMedCount(res.length))
      .catch(() => setMedCount(0));
  }, [user?.id]);

  const url = useProtectedImage(picId);

  return (
    <div className="p-5 border shadow-sm rounded-xl bg-blue-50 flex flex-col gap-3">
      <div className="flex justify-between items-center">
        <div>
          <div className="font-extrabold">Welcome Back</div>
          <div className="text-3xl font-semibold text-blue-600">{user?.name} !</div>
          <div className="text-sm text-gray-500">
            {bloodGroupMap[patientInfo.bloodGroup]}{patientInfo.address ? `, ${patientInfo.address}` : ""}
          </div>
        </div>
        <Avatar src={url} size={100} alt="it's me" radius="xl" />
      </div>
      <div className="gap-3 flex">
        <div className="p-3 rounded-xl bg-violet-200 min-w-[90px]">
          <div className="text-sm">Visits</div>
          {totalVisits === null ? <Skeleton height={22} width={50} mt={4} /> :
            <div className="text-lg font-semibold text-violet-700">{totalVisits}</div>}
        </div>
        <div className="p-3 rounded-xl bg-orange-200 min-w-[90px]">
          <div className="text-sm">Medications</div>
          {medCount === null ? <Skeleton height={22} width={50} mt={4} /> :
            <div className="text-lg font-semibold text-orange-700">{medCount}</div>}
        </div>
      </div>
    </div>
  );
};

export default Welcome;
