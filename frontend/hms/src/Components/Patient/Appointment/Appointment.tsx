import React, { useState, useEffect } from "react";
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { ActionIcon, Button, LoadingOverlay, Modal, SegmentedControl, Select, Text, Textarea } from "@mantine/core";
import { Column } from "primereact/column";
import 'primereact/resources/themes/lara-light-blue/theme.css'
import { Tag } from "primereact/tag";
import { TextInput } from "@mantine/core";
import { IconEdit, IconPlus, IconSearch, IconTrash } from "@tabler/icons-react";
import { useDisclosure } from "@mantine/hooks";
import { getDoctorDropdown } from "../../../Service/DoctorProfileService";
import { DateTimePicker } from "@mantine/dates";
import { useForm } from "@mantine/form";
import { appointmentReason } from "../../../Data/DropDownData";
import { useSelector } from "react-redux";
import {
    cancelAppointment,
  getAppointmentByPatient,
  scheduleAppointment,
} from "../../../Service/AppointmentService";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { formatDateWithTime } from "../../../Utility/DateUtility";
import { modals } from "@mantine/modals";
import { Toolbar } from "primereact/toolbar";
import { tab } from "@testing-library/user-event/dist/tab";
import { toUSVString } from "util";
import dayjs from "dayjs";

interface Country {
  name: string;
  code: string;
}

interface Representative {
  name: string;
  image: string;
}

interface Customer {
  id: number;
  name: string;
  country: Country;
  company: string;
  date: string | Date;
  status: string;
  verified: boolean;
  activity: number;
  representative: Representative;
  balance: number;
}



const Appointment = () => {
  const [loading, setLoading] = useState(false);
  const [opened, { open, close }] = useDisclosure(false);

  const [selectedCustomers, setSelectedCustomers] = useState<Customer[]>([]);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    doctorName: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    reason: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },
    representative: { value: null, matchMode: FilterMatchMode.IN },
    notes: {
      operator: FilterOperator.AND,
      constraints: [{ value: null, matchMode: FilterMatchMode.STARTS_WITH }],
    },

    status: {
      operator: FilterOperator.OR,
      constraints: [{ value: null, matchMode: FilterMatchMode.EQUALS }],
    },
    activity: { value: null, matchMode: FilterMatchMode.BETWEEN },
  });
  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
  const users = useSelector((state: any) => state.user);
  const [appointment, setAppointment] = useState<any[]>([]);
  const [doctors, setDoctors] = useState<any[]>([]);
  const [tab, setTab] = useState<string>("Today");


  const getSeverity = (status: string) => {
    switch (status) {
      case "CANCELLED":
        return "danger";

      case "COMPLETED":
        return "success";

      case "SCHEDULED":
        return "info";

      case "negotiation":
        return "warning";

      default:
        return null;
    }
  };

  useEffect(() => {
    fetchDatas();
    getDoctorDropdown()
      .then((data) => {
        console.log("doctor data", data);
        setDoctors(
          data.map((doctor: any) => ({
            value: "" + doctor.id,
            label: doctor.name,
          }))
        );
      })
      .catch((error) => {
        console.error("Error fetching doctor dropdown:", error);
      });

    }, []);
  // eslint-disable-line react-hooks/exhaustive-deps

  const getCustomers = (data: Customer[]) => {
    return [...(data || [])].map((d) => {
      d.date = new Date(d.date);

      return d;
    });
  };



  const fetchDatas =()=>{
       getAppointmentByPatient(users.profileId)
      .then((data) => {
        console.log("Patient appointments:", data);
        setAppointment(getCustomers(data));
      })
      .catch((error) => {
        console.error("Error fetching patient appointments:", error);
      });
  };
  const timeTemplate = (rowData: any) => {
    return <span>{formatDateWithTime(rowData.appointmentTime)}</span>;
  };





  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const form = useForm({
    initialValues: {
      doctorId: "",
      patientId: users.profileId,
      appointmentTime: new Date(),
      reason: "",
      notes: "",
    },
    validate: {
      doctorId: (value: any) => (!value ? "Doctor is required" : undefined),
      patientId: (value: any) => (!value ? "Patient is required" : undefined),
      appointmentTime: (value: any) =>
        !value ? "Appointment time is required" : undefined,
      reason: (value: any) => (!value ? "Reason is required" : undefined),
    },
  });
  const handleSubmit = (values: any) => {
    // Handle form submission
const payload = {
    ...values,
    appointmentTime: dayjs(values.appointmentTime).format("YYYY-MM-DDTHH:mm:ss")

  };

  console.log("Payload to backend:", payload);

  setLoading(true);
  scheduleAppointment(payload)
      .then((data) => {
        setLoading(false);
        close();
        form.reset();
         fetchDatas();
        successNotification("Appointment scheduled successfully!");
      })
      .catch((error) => {
        errorNotification(
          error?.response?.data?.message ||
            "Error scheduling appointment. Please try again."
        );
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-between tems-center">
        <Button onClick={open} leftSection={<IconPlus />} variant="filled">
          Schedule Appointment
        </Button>
        <TextInput
          leftSection={<IconSearch />}
          fw={400}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
      </div>
    );
  };




  const statusBodyTemplate = (rowData: Customer) => {
    return (
      <Tag value={rowData.status} severity={getSeverity(rowData.status)} />
    );
  };





  const handleDelete = (rowData: any) => {
    // Implement your delete logic here
    modals.openConfirmModal({
    title: <span className="text-xl font-semibold">are you sure ?</span>,
    centered: true,
    children: (
      <Text size="sm">
        Do you really want to cancel appointment? This process cannot be undone.
      </Text>
    ),
    labels: { confirm: 'Confirm', cancel: 'Cancel' },

    onConfirm: () => {
      
        cancelAppointment(rowData.id).then(()=>{
            successNotification("Appointment cancelled successfully");
            setAppointment(appointment.map(app=> app.id == rowData.id?{...app,status:"CANCELLED"}:app))
        }).catch((error) => {
            errorNotification("Failed to cancel appointment");
        });
    },
  });

  };

  const actionBodyTemplate = (rowData:any) => {
    return <div className="flex gap-2">
        <ActionIcon>
            <IconEdit size={20} stroke={1.5} />
        </ActionIcon>
        <ActionIcon  onClick={() => handleDelete(rowData)} color="red">
            <IconTrash size={20} stroke={1.5} />
        </ActionIcon>
    </div>
  };
   const leftToolbarTemplate = () => {
        return (
           <div className="flex flex-wrap gap-2 justify-between tems-center">
        <Button onClick={open} leftSection={<IconPlus />} variant="filled">
          Schedule Appointment
        </Button>
        
      </div>
        );
    };

    const rightToolbarTemplate = () => {
        return <TextInput
          leftSection={<IconSearch />}
          fw={400}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
    };
    const centerToolbarTemplate=()=>{
     return   <SegmentedControl
      value={tab}
      color="primary"
      onChange={setTab}
      data={[
       "Today",
       "Upcoming",
       "Past"
       
      ]}
    />
    }

  const header = renderHeader();

  const filteredAppointment = appointment.filter((appointment)=>{
    const appointmentDate = new Date(appointment.appointmentTime);

    const today = new Date();
    if(tab==="Today"){
      return appointmentDate.toDateString()===today.toDateString();

    }else if(tab==="Upcoming"){
      return appointmentDate > today;
    }else if(tab==="Past"){
      return appointmentDate < today;
    }
    return true;
  })
  return (
    <div className="card">
        <Toolbar
         className="mb-4"  start={leftToolbarTemplate} 
          center={centerToolbarTemplate}
         end={rightToolbarTemplate}></Toolbar>
      <DataTable stripedRows size="small"
        value={filteredAppointment}
        paginator
        
        rows={10}
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        rowsPerPageOptions={[10, 25, 50]}
        dataKey="id"
        selection={selectedCustomers}
       
        filters={filters}
        filterDisplay="menu"
        globalFilterFields={[
          "doctorName",
          "reason",
          "notes",
          "status",
        ]}
        emptyMessage="No Appointment found."
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
      >

        <Column
          field="doctorName"
          header="Doctor"
          sortable
          filter
          filterPlaceholder="Search by doctor"
          style={{ minWidth: "14rem" }}
        />
 
        <Column
          field="appointmentTime"
          header="Appointment Time"
          sortable
          
          style={{ minWidth: "14rem" }}
          body={timeTemplate}
        />
        

        <Column
          field="reason"
          header="Reason"
          sortable
          filter
          filterPlaceholder="Search by reason"
          style={{ minWidth: "14rem" }}
       
        />

        <Column
          field="notes"
          header="Notes"
          sortable
          filter
          filterPlaceholder="Search by notes"
          style={{ minWidth: "14rem" }}
       
        />
         <Column
          field="status"
          header="Status"
          sortable
          filterMenuStyle={{ width: "14rem" }}
          style={{ minWidth: "12rem" }}
          body={statusBodyTemplate}
          filter
        
        />
        <Column
          headerStyle={{ width: "5rem", textAlign: "center" }}
          bodyStyle={{ textAlign: "center", overflow: "visible" }}
          body={actionBodyTemplate}
        />
      </DataTable>
      <Modal
        opened={opened}
        size="lg"
        onClose={close}
        title={
          <div className="text-xl font-semibold text-primary-500">
            Schedule Appointment
          </div>
        }
        centered
      >
        <LoadingOverlay
          visible={loading}
          zIndex={1000}
          overlayProps={{ radius: "sm", blur: 2 }}
        />

        <form
          onSubmit={form.onSubmit(handleSubmit)}
          className="grid grid-col-1 gap-5"
        >
          <Select
            {...form.getInputProps("doctorId")}
            withAsterisk
            data={doctors}
            label="Doctor"
            placeholder="Select Doctor"
          />
          <DateTimePicker
            minDate={new Date()}
            {...form.getInputProps("appointmentTime")}
            withAsterisk
            label="Appointment Time"
            placeholder="Select Date and Time"
          />
          <Select
            data={appointmentReason}
            {...form.getInputProps("reason")}
            withAsterisk
            label="Reason for Appointment"
            placeholder="Enter reason"
          />
          <Textarea
            {...form.getInputProps("notes")}
            withAsterisk
            label="Additional Notes"
            placeholder="Enter any additional notes"
            minRows={3}
          />

          <Button type="submit" variant="filled" fullWidth>
            Submit
          </Button>
        </form>
      </Modal>
    </div>
  );
};

export default Appointment;
