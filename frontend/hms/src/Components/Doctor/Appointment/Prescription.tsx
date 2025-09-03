import { ActionIcon, Button, Card, Divider, Grid, Modal, Text, TextInput, Title } from "@mantine/core";
import { IconEye, IconMedicineSyrup, IconSearchOff, IconTrash } from "@tabler/icons-react";
import { FilterMatchMode, FilterOperator } from "primereact/api";
import { Column } from "primereact/column";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import React, { useEffect, useState } from "react";
import { getPrescriptionByPatientId } from "../../../Service/AppointmentService";
import { formatDate } from "../../../Utility/DateUtility";
import { Tag } from "primereact/tag";
import { useNavigate } from "react-router-dom";
import { useDisclosure } from "@mantine/hooks";

const Prescription = ({ appointment }: any) => {
    const [opened,{open,close}] = useDisclosure(false);
  const [data, setData] = useState<any[]>([]);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const actionBodyTemplate = (rowData:any) => {
    return <div className="flex gap-2">
        <ActionIcon onClick={()=> navigate("/doctor/appointment/"+rowData.appointmentId)}>
            <IconEye size={20} stroke={1.5} />
        </ActionIcon>
        <ActionIcon onClick={()=>handleMedicine(rowData.medicines)}>
            <IconMedicineSyrup size={20} stroke={1.5} />
        </ActionIcon>
        
    </div>
  };
   const [medicineData,setMedicineData] = useState<any[]>([]);
  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;

    setFilters(_filters);
    setGlobalFilterValue(value);
  };
    const navigate = useNavigate();

  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");
   console.log("patientId:", appointment?.patientId);
  useEffect(() => {
if(!appointment?.patientId)return;

    getPrescriptionByPatientId(appointment?.patientId)
    
      .then((res) => {
        console.log("Fetched Prescription data:", res);
        setData(res);
      })
      .catch((err) => {
        console.error("Error fetching Prescription data:", err);
      });
  },[appointment?.patientId]);

  const handleMedicine = (medicines:any[])=>{

    open();
    setMedicineData(medicines);
  }
  const renderHeader = () => {
    return (
      <div className="flex flex-wrap gap-2 justify-end tems-center">
        <TextInput
          leftSection={<IconSearchOff />}
          fw={400}
          value={globalFilterValue}
          onChange={onGlobalFilterChange}
          placeholder="Keyword Search"
        />
      </div>
    );
  };
  const header = renderHeader();

  return (
    <div>
      <DataTable
        header={header}
        stripedRows
        size="small"
        value={data}
        paginator
        rows={10}
        paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
        rowsPerPageOptions={[10, 25, 50]}
        dataKey="id"
        filterDisplay="menu"
        globalFilterFields={["patientName", "notes"]}
        emptyMessage="No Appointment found."
        currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
      >
        <Column field="doctorName" header="Doctor"  />

        <Column
          field="prescriptionDate"
          header="Prescription Date"
          sortable
          style={{ minWidth: "14rem" }}
          body={(rowData) => <>{formatDate(rowData.prescriptionDate)}</>}
        />

   <Column field="doctorName" header="Medicines"  body={(rowData)=>rowData.medicines?.length??0} />

    
        <Column
          field="notes"
          header="Notes"
        
          style={{ minWidth: "14rem" }}
        />
        <Column
          headerStyle={{ width: "5rem", textAlign: "center" }}
          bodyStyle={{ textAlign: "center", overflow: "visible" }}
          body={actionBodyTemplate}
        />
        {/* <Column
               headerStyle={{ width: "5rem", textAlign: "center" }}
               bodyStyle={{ textAlign: "center", overflow: "visible" }}
               body={actionBodyTemplate}
             /> */}
      </DataTable>
       <Modal opened={opened} size="xl" onClose={close} title="Medicines" centered>
<div className="grid grid-cols-2 gap-5">

        {
          medicineData?.map((data:any,index:number)=>(

          
        <Card key={index} shadow="md" radius="md" withBorder padding="lg">

          <Title order={4} mb="sm" >

            {data.name} {data.type}

          </Title>
          <Divider my="sm" />
          <Grid>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Dosage:</Text>
            <Text>{data.dosage}</Text>
           </Grid.Col>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Frequency:</Text>
            <Text>{data.frequency}</Text>
           </Grid.Col>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Duration:</Text>
            <Text>{data.duration}</Text>
           </Grid.Col>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Route:</Text>
            <Text>{data.route}</Text>
           </Grid.Col>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Prescription ID:</Text>
            <Text>{data.prescriptionId}</Text>
           </Grid.Col>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Medicine ID:</Text>
            <Text>{data.medicineId}</Text>
           </Grid.Col>
           <Grid.Col span={6}>
            <Text size="sm" fw={500} >Instructions:</Text>
            <Text>{data.instructions}</Text>
           </Grid.Col>
          </Grid>





        </Card>
          ))}
  
</div>
          {
            medicineData.length ==0&&(
              <Text color="dimmed" size="sm" mt="md" >
                No medicines Prescribed for this appointment.
              </Text>
            )
          }
      </Modal>

    </div>
  );
};

export default Prescription;
