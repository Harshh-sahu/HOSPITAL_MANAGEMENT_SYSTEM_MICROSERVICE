import { Avatar, Skeleton } from "@mantine/core";
import  { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { getUserProfile } from "../../../Service/UserService";
import useProtectedImage from "../../../Utility/useProtectedImage";
import { getDoctor } from "../../../Service/DoctorProfileService";
import { countAppointmentByDoctor } from "../../../Service/AppointmentService";
import { getAllPatient } from "../../../Service/PatientProfileService";

const Welcome = () => {
  const user = useSelector((state: any) => state.user);
  const [picId, setPicId] = useState<string | null>(null);
  const [doctorInfo, setDoctorInfo] = useState<any>({});
  const [totalAppointments, setTotalAppointments] = useState<number | null>(null);
  const [totalPatients, setTotalPatients] = useState<number | null>(null);

  useEffect(() => {
    if (!user) return;

    getUserProfile(user.id)
      .then((res) => setPicId(res))
      .catch((err) => console.error("Error fetching user profile:", err));

    getDoctor(user.profileId)
      .then((res) => setDoctorInfo(res))
      .catch((err) => console.error("Error fetching doctor profile:", err));

    countAppointmentByDoctor(user.profileId)
      .then((res: any[]) => {
        const total = res.reduce((sum: number, m: any) => sum + (m.count ?? 0), 0);
        setTotalAppointments(total);
      })
      .catch((err) => console.error("Error fetching appointment count:", err));

    getAllPatient()
      .then((res: any[]) => setTotalPatients(res.length))
      .catch((err) => console.error("Error fetching patients:", err));
  }, [user?.id]);

  const url = useProtectedImage(picId);

  return (
    <div className="p-5 border shadow-sm rounded-xl bg-blue-50 flex flex-col gap-3">
      <div className="flex justify-between items-center">
        <div>
          <div className="font-extrabold">Welcome Back</div>
          <div className="text-3xl font-semibold text-blue-600">{user?.name} !</div>
          <div className="text-sm text-gray-500">
            {doctorInfo?.specialization}{doctorInfo?.department ? `, ${doctorInfo.department}` : ""}
          </div>
        </div>
        <Avatar src={url} size={100} alt="it's me" radius="xl" />
      </div>
      <div className="gap-3 flex">
        <div className="p-3 rounded-xl bg-violet-200 min-w-[90px]">
          <div className="text-sm">Appointments</div>
          {totalAppointments === null ? (
            <Skeleton height={22} width={50} mt={4} />
          ) : (
            <div className="text-lg font-semibold text-violet-700">{totalAppointments}</div>
          )}
        </div>
        <div className="p-3 rounded-xl bg-orange-200 min-w-[90px]">
          <div className="text-sm">Patients</div>
          {totalPatients === null ? (
            <Skeleton height={22} width={50} mt={4} />
          ) : (
            <div className="text-lg font-semibold text-orange-700">{totalPatients}</div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Welcome;
