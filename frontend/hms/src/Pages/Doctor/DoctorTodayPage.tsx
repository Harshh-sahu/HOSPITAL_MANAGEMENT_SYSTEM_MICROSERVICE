import { Group } from "@mantine/core";
import { IconCalendarCheck } from "@tabler/icons-react";
import TodaySchedule from "../../Components/Doctor/TodaySchedule/TodaySchedule";

const DoctorTodayPage = () => {
  return (
    <div className="p-5">
      <Group gap={8} mb="lg">
        <IconCalendarCheck size={26} className="text-primary-500" />
        <h2 className="text-2xl font-semibold text-primary-600">Today's Schedule</h2>
      </Group>
      <TodaySchedule />
    </div>
  );
};

export default DoctorTodayPage;
