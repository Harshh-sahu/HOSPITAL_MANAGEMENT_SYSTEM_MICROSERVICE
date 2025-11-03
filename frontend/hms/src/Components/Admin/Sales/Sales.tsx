import {
  ActionIcon,
  Button,
  Card,
  Divider,
  Fieldset,
  Grid,
  Group,
  LoadingOverlay,
  Modal,
  NumberInput,
  SegmentedControl,
  Select,
  SelectProps,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import React, { use, useEffect, useState } from "react";

import {
  IconCheck,
  IconDashboard,
  IconEye,
  IconFileText,
  IconHome,
  IconLayoutGrid,
  IconPlus,
  IconSearch,
  IconSearchOff,
  IconTable,
  IconTrash,
} from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { FilterMatchMode } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { Column } from "primereact/column";
import {

  getAllMedicines,

} from "../../../Service/MedicineService";

import { addSale, getAllSaleItem, getAllsales } from "../../../Service/SalesService";
import { formatDate } from "../../../Utility/DateUtility";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { spotlight, Spotlight, SpotlightActionData } from "@mantine/spotlight";
import { getAllPrescriptions, getMedicinesByPrescriptionId } from "../../../Service/AppointmentService";
import { freqMap } from "../../../Data/DropDownData";
import { Toolbar } from "primereact/toolbar";
import MedCard from "../Medicine/MedCard";
import SaleCard from "./SaleCard";

interface SaleItem {
  medicineId: string;
  quantity: number;
}
const Sales = () => {
  const [loading, setLoading] = React.useState(false);
  const [view, setView] = useState<string>("table");
  const form = useForm<any>({
    initialValues: {
      buyerName: "",
      buyerContact: "",
      saleItems: [
        { medicineId: "", quantity: 0 }
      ] as SaleItem[],
    },
    validate: {
      saleItems: {
        medicineId: (value) => (value ? null : "Medicine ID is required"),

        quantity: (value) =>
          value > 0 ? null : "Quantity cannot be negative",
      },
    },
  });
   const [opened,{ open, close }] = useDisclosure(false);
    const [saleItems,setSaleItems] = useState<any[]>([]);
  const [data, setData] = useState<any[]>([]);
  const [Medicine, setMedicine] = useState<any[]>([]);
  const [edit, setEdit] = useState<boolean>(false);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const [actions, setActions] = useState<SpotlightActionData[]>([]);

  const cancel = () => {
    form.reset();
    setEdit(false);
  };

  const addMore = () => {
    form.insertListItem("saleItems", { medicineId: "", quantity: 0 });
  };

  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});

  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");

  useEffect(() => {
    getAllMedicines()
      .then((res) => {
        console.log("Fetched stock medicine data:", res);
        setMedicine(res);
        setMedicineMap(
          res.reduce((acc: any, item: any) => {
            acc[item.id] = item;
            return acc;
          }, {})
        );
      })
      .catch((error) => {
        console.error("Error fetching medicine data:", error);
      });


      getAllPrescriptions().then((res)=>{
         console.table(res);
        setActions(res.map((item:any)=>({
          id: String(item.id),
          label: item.patientName,
          description: `Prescription by Dr. ${item.doctorName} on ${formatDate(item.prescriptionDate)}`,
          onClick:() => handleImport(item)
        })));
      }).catch((error)=>{
        console.error("Error fetching prescription data:", error);
      });
    fetchData();
  }, []);

  const handleImport = (item:any) => {

        setLoading(true);
        getMedicinesByPrescriptionId(item.id).then((res)=>{
         successNotification("Prescription imported successfully");
         setSaleItems(res);
         form.setValues({
          buyerName: item.patientName,
        
          saleItems: res.filter((x:any)=>x.medicineId != null).map((x:any)=>({medicineId: String(x.medicineId), quantity: calculateQuantity(x.frequency,x.duration)}))
         });

         
          console.log("res:", res);
        }).catch((error)=>{
          console.error("Error fetching medicines by prescription ID:", error);
          errorNotification(`Error fetching medicines: ${error.message}`);
        } ).finally(()=>setLoading(false));

    }
    const matches = useMediaQuery('(max-width: 768px)');

    const calculateQuantity = (freq:string, duration:number)=>{
      const freqValue = freqMap[freq];
      if(!freqValue) return 0;

      return Math.ceil(freqValue * duration );
  };

  const fetchData = () => {
    getAllsales()
      .then((res) => {
        console.log("Fetched sales data:", res);
        setData(res);
      })
      .catch((error) => {
        console.error("Error fetching sales data:", error);
      });
  };

  const handleSubmit = (values: any) => {
   let update = false;
    const saleItems = values.saleItems.map((x:any)=>({...x,unitPrice: medicineMap[x.medicineId]?.unitPrice
    }));

    const totalAmount = saleItems.reduce((acc:any, item:any) => acc + (item.unitPrice || 0) * item.quantity, 0);
    console.log("form submitted with value", saleItems);

    setLoading(true);

    addSale({...values, saleItems, totalAmount })
    
      .then((_res) => {

        successNotification("Sale added successfully");
        fetchData();
        form.reset();
        setEdit(false);
      })
      .catch((error) => {
        console.error("Error adding sale:", error);
        errorNotification(`Error adding sale: ${error.message}`);
      })
      .finally(() => setLoading(false));
      console.log("totalAmount", totalAmount);
  };

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };

    _filters["global"].value = value;
    setFilters(_filters);
    setGlobalFilterValue(value);
  };




  const onEdit = (rowData: any) => {
    setEdit(true);
    console.log("rowData:", rowData);
    form.setValues({
      ...rowData,
      name: rowData.name,
      dosage: rowData.dosage,
      category: rowData.category,
      type: rowData.type,
      manufacturer: rowData.manufacturer,
      unitPrice: rowData.unitPrice,
    });
  };

  const handleDetails = (rowData: any) =>{
open();
setLoading(true);
getAllSaleItem(rowData.id).then((res)=>{
  setSaleItems(res);
  console.log("Fetched sale items:", res);
}).catch((error)=>{
  console.error("Error fetching sale items:", error);
  }
  ).finally(()=>setLoading(false));
  }



   const startToolbarTemplate = () => {
      return (
         <Button
            onClick={() => {
              form.reset(); // 👈 ensure add mode clears old id
              setEdit(true);
            }}
            variant="filled"
          >
            Sell Medicine
          </Button>
      )
    }
  
      const rightToolbarTemplate = () => {
         return <div className="md:flex hidden gap-5 items-center">
    
               <SegmentedControl
          value={view}
          size={matches ? "sm":"md"}
          color="primary"
          onChange={setView}
          data={[
            { label: <IconTable />, value: 'table' },
            { label: <IconLayoutGrid />, value: 'card' }
          
          ]}
        />
    
             <TextInput
             className="lg:block hidden"
              leftSection={<IconSearch
                 />}
              fw={400}
              value={globalFilterValue}
              onChange={onGlobalFilterChange}
              placeholder="Keyword Search"
            /></div>
        };
  const renderSelectOption: SelectProps["renderOption"] = ({
    option,
    checked,
  }: any) => (
    <Group flex="1" gap="xs">
      <div className="flex gap-5 items-center">
        {option.label}
        {option?.manufacturer && (
          <span style={{ fontStyle: "italic", color: "gray" }}>
            {option.manufacturer}
            - {option.dosage}
          </span>
        )}
      </div>
      {checked && <IconCheck style={{ marginInlineStart: "auto" }} />}
    </Group>
  );

  const actionBodyTemplate = (rowData: any) => {
    return (
      <div className="flex gap-2">
        <ActionIcon onClick={() => handleDetails(rowData)} color="blue">
          <IconEye size={20} stroke={1.5} />
        </ActionIcon>
      </div>
    );
  };

  const handleSpotlight = ()=>{
   spotlight.open();

  }
  return (
    <div>
      {!edit ? (
          <div>
                
                       <Toolbar
                               className="mb-4 !p-1"
                                 start={startToolbarTemplate} 
                          
                               end={rightToolbarTemplate}></Toolbar>
                 
                       {view==="table"&&!matches? 
        <DataTable
          stripedRows
          size="small"
          value={data}
          paginator
          rows={10}
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          rowsPerPageOptions={[10, 25, 50]}
          dataKey="medicineId"
          filterDisplay="menu"
          globalFilterFields={["name", "manufacturer", "category", "type"]}
          emptyMessage="No Medicine found."
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
        >
          <Column
            field="buyerName"
            header="Buyer"
            sortable

          />
          <Column field="buyerContact" header="Contact" />
          {/* <Column
            field="Prescription"
            header="Prescription"
            body={(rowData) => capitalizeFirstLetter(rowData.buyerPrescription) ?? ""}
          /> */}
          <Column
            field="totalAmount"
            header="Total Amount"
            sortable
          />
          <Column
            field="saleDate"
            header="Sale Date"
            sortable
            body={(rowData) => formatDate(rowData.saleDate) ?? ""}
          />
          <Column header="Actions" body={actionBodyTemplate} />
  
        </DataTable>
      
          :         <div className="grid lg:grid-cols-4 md:grid-cols-3 sm:grid-cols-2 grid-cols-1  gap-5">
                      {
              
                        data?.map((app)=> <SaleCard key={app.id} onView={() => handleDetails(app)}
                            buyerName={app.buyerName}
    buyerContact={app.buyerContact}
    saleDate={app.saleDate}
    totalAmount={app.totalAmount}

 />)
                      }
                      {
                        data?.length===0 && <div className="col-span-4 text-center text-gray-500">No Medicine found.</div>
                      }
                    </div>}
                      </div>) : (<div>

        <div className="mb-5 flex items-center justify-between">
            <h3 className="text-xl font-medium text-primary-500" >Sell Medicine</h3>

            <Button variant="filled" leftSection={<IconPlus />} onClick={handleSpotlight} >
              Import Prescription
            </Button>

        </div>
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <LoadingOverlay visible={loading} />
             <Fieldset
            className="grid gap-5"
            legend={
              <span className="text-lg font-medium text-primary-500">
                Buyer information
              </span>
            }
            radius="md"
          >
          <div className="grid gap-8 sm:grid-cols-2">
              <TextInput
              withAsterisk
              placeholder="Enter Buyer Name"
              label="Buyer Name"
              {...form.getInputProps("buyerName")}
            />
            <NumberInput
            maxLength={10}

              withAsterisk
              label="Buyer Contact"
              placeholder="Enter Buyer Contact Number"
              {...form.getInputProps("buyerContact")}
            />
          </div>

          </Fieldset>
          <Fieldset
            className="grid gap-5"
            legend={
              <span className="text-lg font-medium text-primary-500">
                Medicine information
              </span>
            }
            radius="md"
          >
            <div className="grid gap-4 sm:grid-cols-5">
              {form.values.saleItems.map((item: any, index: number) => (
                <React.Fragment key={index}>
                  <div className="col-span-2">
                    <Select
                      renderOption={renderSelectOption}
                      {...form.getInputProps(`saleItems.${index}.medicineId`)}
                      label="Medicine"
                      placeholder="Select Medicine"
                      data={Medicine.filter(x=>!form.values.saleItems.some((item1:any,idx:any )=>item1.medicineId ===x.id &&idx!==index)).map((item) => ({
                        ...item,
                        value: "" + item.id,
                        label: item.name,
                      }))}
                    />
                  </div>

                  <div className="col-span-2">
                    <NumberInput rightSectionWidth={80} rightSection={<div className="text-xs flex gap-1 text-gray-500">Stock: {medicineMap[item.medicineId]?.stock || 0}</div>}
                      {...form.getInputProps(`saleItems.${index}.quantity`)}
                      label="Quantity"
                      placeholder="Enter Quantity in stock"
                      min={0}
                      max={medicineMap[item.medicineId]?.stock || 0}
                      clampBehavior="strict"
                    />
                  </div>

                  <div  className="flex items-center justify-between">

                     {(item.quantity && item.medicineId) ?<div>
                        Total Price: ₹{item.quantity} X{medicineMap[item.medicineId]?.unitPrice} = ₹
                        {item.quantity *
                          (medicineMap[item.medicineId]?.unitPrice ?? 0)}
                      </div> : null}
                    <ActionIcon color="red" onClick={() => form.removeListItem("saleItems", index)}>

<IconTrash size={16}/>

                    </ActionIcon>
                  </div>
                </React.Fragment>
              ))}
            </div>

            <div className="flex justify-center items-center">
              <Button
              onClick={addMore}
              variant="outline" leftSection={<IconPlus />}>
                
                Add More
              </Button>
            </div>
          </Fieldset>

          <div className="flex items-center gap-5 justify-center mt-4">
            <Button
              loading={loading}
              type="submit"
              className="w-full"
              variant="filled"
              color="primary"
            >
             Sell Medicine
            </Button>
            <Button
              loading={loading}
              className="w-full"
              variant="filled"
              color="red"
              onClick={() => {
                setEdit(false);
              }}
            >
              Cancel
            </Button>
          </div>
        </form>
        </div>
      )}
           <Modal opened={opened} size="xl" onClose={close} title="Sold Medicines" centered>
    <div className="grid sm:grid-cols-2 gap-5">
    
            {
              saleItems?.map((data:any,index:number)=>(
    
              
            <Card key={index} shadow="md" radius="md" withBorder padding="lg">
    
              <Title order={4} mb="sm" >
    
                {medicineMap[data.medicineId]?.name} - {medicineMap[data.medicineId]?.dosage}(<span className="text-gray-500">{medicineMap[data.medicineId]?.manufacturer}</span>)
    
              </Title>

<Text size="sm" color="dimmed" >
 {data.batchNo}
</Text>
              <Divider my="xs" />
              <Grid>
               <Grid.Col span={4}>
                <Text size="sm" fw={500} >Quantity:</Text>
                <Text>{data.quantity}</Text>
               </Grid.Col>
               <Grid.Col span={4}>
                <Text size="sm" fw={500} >Unit Price:</Text>
                <Text>₹{data.unitPrice}</Text>
               </Grid.Col>
               <Grid.Col span={4}>
                <Text size="sm" fw={500} >Total:</Text>
                <Text>₹{data.quantity * data.unitPrice}</Text>
               </Grid.Col>
              </Grid>
    
    
    
    
    
            </Card>
              ))}
      
    </div>
              {
                saleItems.length ===0&&(
                  <Text color="dimmed" size="sm" mt="md" >
                    No medicines Prescribed for this appointment.
                  </Text>
                )
              }
          </Modal>
      <Spotlight
        actions={actions}
        nothingFound="Nothing found..."
        highlightQuery
        searchProps={{
          leftSection: <IconSearch size={20} stroke={1.5} />,
          placeholder: 'Search...',
        }}
      />
  
      </div>
  );
};

export default Sales;
